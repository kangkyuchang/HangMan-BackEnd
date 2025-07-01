package com.kkc.hangman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HangMan {

	private HashMap<Integer, String> winner;
	private boolean isStart;
	private int step;
	private final int MAXSTEP;
	private List<String> question;
	private List<String> correctAnswer;
	private int life;
	private List<String[]> correctAnswerAlphabet;
	private List<String[]> correctAlphabet;
	private List<String> usedAlphabet;
	
	public HangMan() {
		setQuestion();
		MAXSTEP = question.size();
		init();
	}
	
	public void init() {
		isStart = false;
		step = 0;
		winner = new HashMap<Integer, String>();
		initStep();
	}
	
	private void initStep() {
		correctAnswerAlphabet = new ArrayList<String[]>();
		correctAlphabet = new ArrayList<String[]>();
		usedAlphabet = new ArrayList<String>();
		life = 7;
		String correctAnswer = this.correctAnswer.get(step);
		for(int i = 0; i < correctAnswer.length(); i++) {
			correctAnswerAlphabet.add(decompose(correctAnswer.charAt(i)));
			String[] str = {"", "", ""};
			correctAlphabet.add(str);
		}
//		for(String[] str: correctAnswerAlphabet) {
//		for(String s : str) {
//			System.out.print(" " + s);
//		}
//	}
	}
	
	private void setQuestion() {
		question = new ArrayList<String>();
		correctAnswer = new ArrayList<String>();
		question.add("기계학습은 사람의 OO 능력을 컴퓨터로 구현하기 위해 빅데이터로 학습시켜 새로운 데이터를 예측하는 인공지능 기술이다.");
		correctAnswer.add("학습");
		question.add("비지도 학습은 OO이 없는 데이터로 학습하여 스스로 데이터 간의 패턴이나 구조를 찾아내어 비슷한 것끼리 묶는다.");
		correctAnswer.add("정답");
		question.add("동일한 목적의 인공지능이라도 학습 OO에 따라 지도학습이 될 수도 있고 비지도 학습이 될 수도 있다.");
		correctAnswer.add("상황");
		question.add("게임에서 플레이어가 아닌 봇과 대결할 때 해당 봇의 기계학습 유형은?");
		correctAnswer.add("강화학습");
		question.add("넷플릭스와 같은 OTT에서 제공하는 영상 추천 시스템의 기계학습 유형은?");
		correctAnswer.add("비지도학습");
	}
	
	public boolean isStart() {
		return isStart;
	}
	
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
	public int getStep() {
		return step;
	}
	
	public List<String> getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(List<String> correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public void putWinner(String name) {
		winner.put(step, name);
	}
	
	public void nextStep() {
		if(++step >= MAXSTEP) {
			isStart = false;
			return;
		}
		correctAnswerAlphabet.clear();
		correctAlphabet.clear();
		usedAlphabet.clear();
		initStep();
	}
	
	public String getQuestion() {
		return question.get(step);
	}
	
	public void setQuestion(List<String> question) {
		this.question = question;
	}
	
	public int getLife() {
		return life;
	}
	
	public void setLife(int life) {
		this.life = life;
	}
	
	public int getMaxStep() { return MAXSTEP; }
	
	public boolean isCorrectAlphabet(String alphabet) {
		boolean isCorrect = false;
		for(int i = 0; i < correctAnswerAlphabet.size(); i++) {
			String[] str = correctAnswerAlphabet.get(i);
			for(int j = 0; j < str.length; j++) {
				if(str[j].equals(alphabet)) {
					String[] s = correctAlphabet.get(i);
					s[j] = alphabet;
					correctAlphabet.set(i, s);
					isCorrect = true;
				}
			}
		}
		usedAlphabet.add(alphabet);
		return isCorrect;
	}
	
	public boolean isCorrectAnswer(String answer) {
		return correctAnswer.get(step).equals(answer);
	}
	
	public List<String[]> getCorrectAlphabet() {
		return correctAlphabet;
	}

	public List<String> getUsedAlphabet() {
		return usedAlphabet;
	}
	
	public boolean isUsedAlphabet(String alphabet) {
		return usedAlphabet.contains(alphabet);
	}
	
	public ArrayList<String> getRanking() {
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(int i : winner.keySet()) {
			int count = 1;
			String name = winner.get(i);
			if(map.containsKey(name)) {
				count += map.get(name);
			}
			map.put(name, count);
		}
		int size = map.keySet().size();
		for(int i = 0; i < size; i++) {
			String max = "";
			Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()) {
				String s = iterator.next();
				if(max.equals("")) {
					max = s;
				}
				else {
					if(map.get(max) < map.get(s)) 
						max = s;
				}
			}
			list.add(max);
			map.remove(max);
		}
		return list;
	}

	private String[] decompose(char c) { 
		int code = c;
		int unicode = code - 44032;
		int choseongIndex = (unicode / 28) / 21;
		int moenumIndex = (unicode / 28) % 21;
		int jongseongIndex = (unicode % 28) - 1;
		
		HashMap<Character, Character> map = new HashMap<Character, Character>();
		map.put((char) 4352, (char) 12593);
		map.put((char) 4353, (char) 12594);
		map.put((char) 4354, (char) 12596);
		map.put((char) 4355, (char) 12599);
		map.put((char) 4356, (char) 12600);
		map.put((char) 4357, (char) 12601);
		map.put((char) 4358, (char) 12609);
		map.put((char) 4359, (char) 12610);
		map.put((char) 4360, (char) 12611);
		map.put((char) 4361, (char) 12613);
		map.put((char) 4362, (char) 12614);
		map.put((char) 4363, (char) 12615);
		map.put((char) 4364, (char) 12616);
		map.put((char) 4365, (char) 12617);
		map.put((char) 4366, (char) 12618);
		map.put((char) 4367, (char) 12619);
		map.put((char) 4368, (char) 12620);
		map.put((char) 4369, (char) 12621);
		map.put((char) 4370, (char) 12622);
		
		map.put((char) 4520, (char) 12593);
		map.put((char) 4521, (char) 12594);
		map.put((char) 4522, (char) 12595);
		map.put((char) 4523, (char) 12596);
		map.put((char) 4524, (char) 12597);
		map.put((char) 4525, (char) 12598);
		map.put((char) 4526, (char) 12599);
		map.put((char) 4527, (char) 12601);
		map.put((char) 4528, (char) 12602);
		map.put((char) 4529, (char) 12603);
		map.put((char) 4530, (char) 12604);
		map.put((char) 4531, (char) 12605);
		map.put((char) 4532, (char) 12606);
		map.put((char) 4533, (char) 12607);
		map.put((char) 4534, (char) 12608);
		map.put((char) 4535, (char) 12609);
		map.put((char) 4536, (char) 12610);
		map.put((char) 4537, (char) 12612);
		map.put((char) 4538, (char) 12613);
		map.put((char) 4539, (char) 12614);
		map.put((char) 4540, (char) 12615);
		map.put((char) 4541, (char) 12616);
		map.put((char) 4542, (char) 12618);
		map.put((char) 4543, (char) 12619);
		map.put((char) 4544, (char) 12620);
		map.put((char) 4545, (char) 12621);
		map.put((char) 4546, (char) 12622);
		
		String choseong = String.valueOf(map.get((char) (4352 + choseongIndex)));
		String moeum = String.valueOf((char) (12623 + moenumIndex));
		String jongseong = jongseongIndex > -1 ? String.valueOf(map.get((char) (4520 + jongseongIndex))) : "";

		String[] array = { choseong, moeum, jongseong };
		return array;
	}
}
