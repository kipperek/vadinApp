package pl.vadinOne.web;

public class Msg {
	private String who;
	private String msg;
	
	
	
	public Msg(String who, String msg) {
		this.who = who;
		this.msg = msg;
	}
	public String getWho() {
		return who;
	}
	public void setWho(String who) {
		this.who = who;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
