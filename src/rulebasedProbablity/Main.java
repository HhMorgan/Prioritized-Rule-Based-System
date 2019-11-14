package rulebasedProbablity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		ArrayList<Rule> program = new ArrayList<Rule>();
		String filename = "program.txt";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				Rule rule = new Rule();
				String lineTrimmed = line.replaceAll("\\s+", "");
				if (lineTrimmed.contains("==>")) {
					rule.setType(0);
				} else {
					rule.setType(1);
				}
				String[] ruleString = lineTrimmed.split("<=>|==>");
				String head = ruleString[0];
				String[] simpigation = head.split("\\\\");

				if (simpigation.length == 2) {
					String[] kept = simpigation[0].split(",");
					String[] removed = simpigation[1].split(",");
					rule.setKept(kept);
					rule.setRemoved(removed);
				} else {
					rule.setHead(head.split(","));
				}

				String[] body = ruleString[1].split("pragma");
				String[] bodyNotPrag = body[0].split("\\|");
				String[] guard = bodyNotPrag[0].split(",");
				String[] effect = bodyNotPrag[1].split(",");

				rule.setGuard(guard);
				rule.setConsequences(effect);
				rule.setPragma(body[1]);
				program.add(rule);
			}
			reader.close();
		} catch (Exception e) {
			System.err.format("Exception occurred trying to read '%s'.", filename);
			e.printStackTrace();
		}
		for (Rule r : program)
			System.out.println(r);

		String chrProgram = ":-use_module(library(chr)).\n:-chr_constraint ";
		ArrayList<String> addedSoFar = new ArrayList<String>();
		for (Rule r : program) {
			if (r.getHead() != null) {
				String[] head = r.getHead();
				for (int i = 0; i < head.length; i++) {
					if (!addedSoFar.contains(head[i])) {
						chrProgram += head[i] + "/0," + head[i] + "/1,";
						addedSoFar.add(head[i]);
					}
				}
			} else {
				String[] kept = r.getKept();
				String[] removed = r.getRemoved();
				for (int i = 0; i < kept.length; i++) {
					if (!addedSoFar.contains(kept[i])) {
						chrProgram += kept[i] + "/0," + kept[i] + "/1,";
						addedSoFar.add(kept[i]);
					}
				}
				for (int i = 0; i < removed.length; i++) {
					if (!addedSoFar.contains(removed[i])) {
						chrProgram += removed[i] + "/0," + removed[i] + "/1,";
						addedSoFar.add(removed[i]);
					}
				}
			}
		}
		chrProgram += "start/0,conflictdone/0,fire/0,id/1,history/1,match/2,match/3.\n\n";

		addedSoFar = new ArrayList<String>();
		for (Rule r : program) {
			if (r.getHead() != null) {
				String[] head = r.getHead();
				for (int i = 0; i < head.length; i++) {
					if (!addedSoFar.contains(head[i])) {
						chrProgram += "id(I), " + head[i] + " <=> " + head[i] + "(I), I1 is I + 1, id(I1).\n";
						addedSoFar.add(head[i]);
					}
				}
			} else {
				String[] kept = r.getKept();
				String[] removed = r.getRemoved();
				for (int i = 0; i < kept.length; i++) {
					if (!addedSoFar.contains(kept[i])) {
						chrProgram += "id(I), " + kept[i] + " <=> " + kept[i] + "(I), I1 is I + 1, id(I1).\n";
						addedSoFar.add(kept[i]);
					}
				}
				for (int i = 0; i < removed.length; i++) {
					if (!addedSoFar.contains(removed[i])) {
						chrProgram += "id(I), " + removed[i] + " <=> " + removed[i] + "(I), I1 is I + 1, id(I1).\n";
						addedSoFar.add(removed[i]);
					}
				}
			}
		}

		chrProgram += "\n";

		int ruleCounter = 1;
		for (Rule r : program) {
			int literalCounter = 1;
			String IDString = "";
			if (r.getHead() != null) {
				String[] head = r.getHead();
				chrProgram += "start, ";
				for (int i = 0; i < head.length; i++) {
					if (i < head.length - 1) {
						chrProgram += head[i] + "(ID" + literalCounter + "), ";
						IDString += "ID" + literalCounter + ",";
						literalCounter++;
					} else {
						chrProgram += head[i] + "(ID" + literalCounter + ")";
						IDString += "ID" + literalCounter;
					}
				}
			} else {
				chrProgram += "start, ";
				String[] kept = r.getKept();
				String[] removed = r.getRemoved();
				for (int i = 0; i < kept.length; i++) {
					chrProgram += kept[i] + "(ID" + literalCounter + "), ";
					IDString += "ID" + literalCounter + ",";
					literalCounter++;
				}
				for (int i = 0; i < removed.length; i++) {
					if (i < removed.length - 1) {
						chrProgram += removed[i] + "(ID" + literalCounter + "), ";
						IDString += "ID" + literalCounter + ",";
						literalCounter++;
					} else {
						chrProgram += removed[i] + "(ID" + literalCounter + ")";
						IDString += "ID" + literalCounter;
					}
				}
			}
			chrProgram += " ==> match(r" + ruleCounter + "," + r.getPragma() + ",[" + IDString + "]).\n";
			ruleCounter++;
		}
		chrProgram += "\n";
		chrProgram += "start <=> conflictdone.";
		chrProgram += "\n";

		chrProgram += "history(L),conflictdone\\match(R,_,IDs) <=> member((R,FIDs),L), sort(IDs,II), sort(FIDs,II) | true.\n\n";
		chrProgram += "conflictdone,match(_,O1,_)\\ match(_,O2,_) <=> O1>O2 | true.\n";
		chrProgram += "conflictdone,match(_,O1,_)\\ match(_,O2,_) <=> O1=O2, R is random(2), R = 0 | true.\n\n";
		chrProgram += "conflictdone <=> fire.\n\n";

		ruleCounter = 1;
		for (Rule r : program) {
			int literalCounter = 1;
			String IDString = "";
			if (r.getHead() != null) {
				String[] head = r.getHead();

				for (int i = 0; i < head.length; i++) {
					if (i < head.length - 1) {
						chrProgram += head[i] + "(ID" + literalCounter + "),";
						IDString += "ID" + literalCounter + ",";
						literalCounter++;
					} else {
						chrProgram += head[i] + "(ID" + literalCounter + ")";
						IDString += "ID" + literalCounter;
					}
				}
			} else {

				String[] kept = r.getKept();
				String[] removed = r.getRemoved();
				for (int i = 0; i < kept.length; i++) {
					if (i < kept.length - 1) {
						chrProgram += kept[i] + "(ID" + literalCounter + "),";
						IDString += "ID" + literalCounter + ",";
						literalCounter++;
					} else {
						chrProgram += kept[i] + "(ID" + literalCounter + ")";
						IDString += "ID" + literalCounter + ",";
						literalCounter++;
					}
				}
				chrProgram += "\\";
				for (int i = 0; i < removed.length; i++) {

					chrProgram += removed[i] + "(ID" + literalCounter + "),";
					if (i < removed.length - 1) {
						IDString += "ID" + literalCounter + ",";
						literalCounter++;
					} else {
						IDString += "ID" + literalCounter;
					}

				}
			}
			String consequence = "";
			for (String c : r.getConsequences()) {
				consequence += c + ",";
			}
			if (r.getType() == 0) { // propagation
				chrProgram += "\\history(L),fire,match(r" + ruleCounter + ",_,[" + IDString + "]) <=> print('fired r"
						+ ruleCounter + "'),nl," + consequence + "history([(r" + ruleCounter + ",[" + IDString
						+ "])|L]),start.";
			} else {
				if (r.getHead() != null) {
					chrProgram += ",history(L),fire,match(r" + ruleCounter + ",_,[" + IDString + "]) <=> print('fired r"
							+ ruleCounter + "'),nl," + consequence + "history([(r" + ruleCounter + ",[" + IDString
							+ "])|L]),start.";
				} else {
					chrProgram += "history(L),fire,match(r" + ruleCounter + ",_,[" + IDString + "]) <=> print('fired r"
							+ ruleCounter + "'),nl," + consequence + "history([(r" + ruleCounter + ",[" + IDString
							+ "])|L]),start.";
				}
			}
			chrProgram += "\n";
			ruleCounter++;
		}

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("resolution.pl"));
			writer.write(chrProgram);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		System.out.println(chrProgram);

	}
}
