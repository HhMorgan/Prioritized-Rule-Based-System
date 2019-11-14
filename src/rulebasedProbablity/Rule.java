package rulebasedProbablity;

import java.util.Arrays;

public class Rule {
	private String[] head;
	private String[] kept;
	private String[] removed;
	private String[] guard;
	private String[] consequences;
	private String pragma;
	private int type;

	public Rule() {
	}

	public String toString() {
		String result = "";
		result += (head != null) ? Arrays.toString(head) + "," : "";
		result += (kept != null) ? Arrays.toString(kept) + "," : "";
		result += (removed != null) ? Arrays.toString(removed) + "," : "";
		result += (guard != null) ? Arrays.toString(guard) + "," : "";
		result += (consequences != null) ? Arrays.toString(consequences) + "," : "";
		result += (pragma != null) ? pragma : "";
		return result;
	}

	public String[] getHead() {
		return head;
	}

	public String[] getKept() {
		return kept;
	}

	public String[] getRemoved() {
		return removed;
	}

	public String[] getGuard() {
		return guard;
	}

	public String[] getConsequences() {
		return consequences;
	}

	public String getPragma() {
		return pragma;
	}
	
	public int getType() {
		return type;
	}

	public void setHead(String[] head) {
		this.head = head;
	}

	public void setKept(String[] kept) {
		this.kept = kept;
	}

	public void setRemoved(String[] removed) {
		this.removed = removed;
	}

	public void setGuard(String[] guard) {
		this.guard = guard;
	}

	public void setConsequences(String[] consequences) {
		this.consequences = consequences;
	}

	public void setPragma(String body) {
		this.pragma = body;
	}
	
	public void setType(int type) {
		this.type = type;
	}

}
