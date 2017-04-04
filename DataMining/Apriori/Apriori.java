import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {

	private static String fileName = "gene_expression.txt";
	private static int support = 50;
    private static int confidence = 60;
	private static Map<String, Integer> fi_count = new HashMap<String, Integer>();

	public static void main(String[] args) {

		// Parse data
		List<Transaction> transactions = new ArrayList<Transaction>();
		parse(transactions);

		// Rules
		List<Rule> rules = new ArrayList<Rule>();
		part1(transactions);
		apriori(transactions, rules);
		System.out.println("Total number of rules generated : " + rules.size());

		template1Queries(rules);
		template2Queries(rules);
		template3Queries(rules);

	}
	public static void part1(List<Transaction> transactions){
		int support = 70;
		apriori_part1(transactions,support);
		support = 60;
		apriori_part1(transactions,support);
		support = 50;
		apriori_part1(transactions,support);
		support = 40;
		apriori_part1(transactions,support);
	}

	public static void template1Queries(List<Rule> rules) {
		List<String> itemList = new ArrayList<String>();
		// Query 1
		List<Rule> q1_result = new ArrayList<>();
		itemList.add("G6_UP");
		int query1 = template1(rules, "RULE", "ANY", itemList, q1_result);
		System.out.println("Number of rules generated from query(RULE HAS ANY OF G6_UP) : " + query1);
		dislayRules(q1_result);

		// Query2
		// RULE HAS 1 OF G1_UP
		itemList = new ArrayList<String>();
		List<Rule> q2_result = new ArrayList<>();
		itemList.add("G1_UP");
		int query2 = template1(rules, "RULE", Integer.toString(1), itemList, q2_result);
		System.out.println("Number of rules generated from query(RULE HAS 1 OF G1_UP) : " + query2);
		dislayRules(q2_result);

		// Query3
		// RULE HAS 1 OF (G1_UP, G10_DOWN)
		itemList = new ArrayList<String>();
		List<Rule> q3_result = new ArrayList<>();
		itemList.add("G1_UP");
		itemList.add("G10_Down");
		int query3 = template1(rules, "RULE", Integer.toString(1), itemList, q3_result);
		System.out.println("Number of rules generated from query(RULE HAS 1 OF (G1_UP, G10_DOWN)) : " + query3);
		dislayRules(q3_result);

		// Query 4
		itemList = new ArrayList<String>();
		List<Rule> q4_result = new ArrayList<>();
		itemList.add("G6_UP");
		int query4 = template1(rules, "BODY", "ANY", itemList, q4_result);
		System.out.println("Number of rules generated from query(BODY HAS ANY OF G6_UP) : " + query4);
		dislayRules(q4_result);

		// Query 5
		itemList = new ArrayList<String>();
		List<Rule> q5_result = new ArrayList<>();
		itemList.add("G72_UP");
		int query5 = template1(rules, "BODY", "NONE", itemList, q5_result);
		System.out.println("Number of rules generated from query(BODY HAS NONE OF G72_UP) : " + query5);
		dislayRules(q5_result);

		// Query6
		// BODY HAS 1 OF (G1_UP, G10_DOWN)
		itemList = new ArrayList<String>();
		List<Rule> q6_result = new ArrayList<>();
		itemList.add("G1_UP");
		itemList.add("G10_Down");
		int query6 = template1(rules, "BODY", Integer.toString(1), itemList, q6_result);
		System.out.println("Number of rules generated from query(RULE HAS 1 OF (G1_UP, G10_DOWN)) : " + query6);
		dislayRules(q6_result);

		// Query 7
		itemList = new ArrayList<String>();
		List<Rule> q7_result = new ArrayList<>();
		itemList.add("G6_UP");
		int query7 = template1(rules, "HEAD", "ANY", itemList, q7_result);
		System.out.println("Number of rules generated from query(HEAD HAS ANY OF G6_UP) : " + query7);
		dislayRules(q7_result);

		// HEAD HAS NONE OF (G1_UP, G6_UP)
		// Query 8
		itemList = new ArrayList<String>();
		List<Rule> q8_result = new ArrayList<>();
		itemList.add("G1_UP");
		itemList.add("G6_UP");
		int query8 = template1(rules, "HEAD", "NONE", itemList, q8_result);
		System.out.println("Number of rules generated from query(HEAD HAS NONE OF G1_UP,G6_UP) : " + query8);
		dislayRules(q8_result);

		// HEAD HAS 1 OF (G6_UP, G8_UP)
		// Query 9
		itemList = new ArrayList<String>();
		List<Rule> q9_result = new ArrayList<>();
		itemList.add("G6_UP");
		itemList.add("G8_UP");
		int query9 = template1(rules, "HEAD", Integer.toString(1), itemList, q9_result);
		System.out.println("Number of rules generated from query(HEAD HAS 1 OF G6_UP,G8_UP) : " + query9);
		dislayRules(q9_result);

		// RULE HAS 1 OF G1_UP, G6_UP, G72_UP
		// Query 10
		itemList = new ArrayList<String>();
		List<Rule> q10_result = new ArrayList<>();
		itemList.add("G1_UP");
		itemList.add("G6_UP");
		itemList.add("G72_UP");
		int query10 = template1(rules, "RULE", Integer.toString(1), itemList, q10_result);
		System.out.println("Number of rules generated from query(RULE HAS 1 OF G1_UP, G6_UP, G72_UP) : " + query10);
		dislayRules(q10_result);

		// RULE HAS ANY OF (G1_UP, G6_UP, G72_UP)
		// Query 11
		itemList = new ArrayList<String>();
		List<Rule> q11_result = new ArrayList<>();
		itemList.add("G1_UP");
		itemList.add("G6_UP");
		itemList.add("G72_UP");
		int query11 = template1(rules, "RULE", "ANY", itemList, q11_result);
		System.out.println("Number of rules generated from query(RULE HAS ANY OF G1_UP, G6_UP, G72_UP) : " + query11);
		dislayRules(q11_result);
	}

	public static void template2Queries(List<Rule> rules) {
		List<Rule> result21 = new ArrayList<>();
		int query21 = template2(rules, "RULE", 3, result21);
		System.out.println("Number of rules generated from query(SIZE OF RULE>=3) : " + query21);
		dislayRules(result21);

		List<Rule> result22 = new ArrayList<>();
		int query22 = template2(rules, "BODY", 2, result22);
		System.out.println("Number of rules generated from query(SIZE OF BODY>=2) : " + query22);
		dislayRules(result21);

		List<Rule> result23 = new ArrayList<>();
		int query23 = template2(rules, "HEAD", 2, result23);
		System.out.println("Number of rules generated from query(SIZE OF HEAD>=2) : " + query23);
		dislayRules(result23);
	}

	public static void template3Queries(List<Rule> rules) {
		List<String> itemList = new ArrayList<String>();
		//BODY HAS ANY OF G1_UP AND HEAD HAS 1 OF G59_UP
        List<Rule> result31_1 = new ArrayList<>();
        List<Rule> result31_2 = new ArrayList<>();
        itemList = new ArrayList<>();
        itemList.add("G1_UP");
        int query31_1 = template1(rules, "BODY", "ANY" , itemList,result31_1);
        itemList = new ArrayList<>();
        itemList.add("G59_UP");
        int query31_2 = template1(rules, "HEAD", Integer.toString(1) , itemList,result31_2);
        //System.out.println("BODY HAS ANY OF G1_UP...");
        //dislayRules(result31_1);
        //System.out.println("HEAD HAS 1 OF G59_UP");
        //dislayRules(result31_2);
        List<Rule> result31 = new ArrayList<>();
        int query31 = template3(result31_1,"AND",result31_2,result31);
        System.out.println("Number of rules generated from query(BODY HAS ANY OF G1_UP AND HEAD HAS 1 OF G59_UP) : " + query31);
        //dislayRules(result31);


        //BODY HAS ANY OF G1_UP OR HEAD HAS 1 OF G6_UP
        List<Rule> result32_1 = new ArrayList<>();
        List<Rule> result32_2 = new ArrayList<>();
        itemList = new ArrayList<>();
        itemList.add("G1_UP");
        int query32_1 = template1(rules, "BODY", "ANY" , itemList,result32_1);
        itemList = new ArrayList<>();
        itemList.add("G6_UP");
        int query32_2 = template1(rules, "HEAD", Integer.toString(1) , itemList,result32_2);
        //System.out.println("BODY HAS ANY OF G1_UP...");
        //dislayRules(result32_1);
        //System.out.println("HEAD HAS 1 OF G6_UP");
        //dislayRules(result32_2);
        List<Rule> result32 = new ArrayList<>();
        int query32 = template3(result32_1,"OR",result32_2,result32);
        System.out.println("Number of rules generated from query(BODY HAS ANY OF G1_UP OR HEAD HAS 1 OF G6_UP) : " + query32);
        //dislayRules(result32);


        //BODY HAS 1 OF G1_UP OR HEAD HAS 2 OF G6_UP
        List<Rule> result33_1 = new ArrayList<>();
        List<Rule> result33_2 = new ArrayList<>();
        itemList = new ArrayList<>();
        itemList.add("G1_UP");
        int query33_1 = template1(rules, "BODY", Integer.toString(1) , itemList,result33_1);
        itemList = new ArrayList<>();
        itemList.add("G6_UP");
        int query33_2 = template1(rules, "HEAD", Integer.toString(2) , itemList,result33_2);
        //System.out.println("BODY HAS 1 OF G1_UP...");
        //dislayRules(result33_1);
        //System.out.println("HEAD HAS 2 OF G6_UP");
        //dislayRules(result33_2);
        List<Rule> result33 = new ArrayList<>();
        int query33 = template3(result33_1,"OR",result33_2,result33);
        System.out.println("Number of rules generated from query(BODY HAS 1 OF G1_UP OR HEAD HAS 2 OF G6_UP) : " + query33);
        //dislayRules(result33);


        //HEAD HAS 1 OF G1_UP AND BODY HAS 0 OF DISEASE
        List<Rule> result34_1 = new ArrayList<>();
        List<Rule> result34_2 = new ArrayList<>();
        itemList = new ArrayList<>();
        itemList.add("G1_UP");
        int query34_1 = template1(rules, "HEAD", Integer.toString(1) , itemList,result34_1);
        itemList = new ArrayList<>();
        itemList.add("ALL");
        itemList.add("AML");
        itemList.add("Breast Cancer");
        itemList.add("Colon Cancer");
        int query34_2 = template1(rules, "BODY", "NONE" , itemList,result34_2);
        //System.out.println("HEAD HAS 1 OF G1_UP...");
        //dislayRules(result34_1);
        //System.out.println("BODY HAS 0 OF DISEASE");
        //dislayRules(result34_2);
        List<Rule> result34 = new ArrayList<>();
        int query34 = template3(result34_1,"AND",result34_2,result34);
        System.out.println("Number of rules generated from query(HEAD HAS 1 OF G1_UP AND BODY HAS 0 OF DISEASE) : " + query34);
        //dislayRules(result34);



        //HEAD HAS 1 OF DISEASE OR RULE HAS 1 OF (G72_UP, G96_DOWN)
        List<Rule> result35_1 = new ArrayList<>();
        List<Rule> result35_2 = new ArrayList<>();
        itemList = new ArrayList<>();
        itemList.add("ALL");
        itemList.add("AML");
        itemList.add("Breast Cancer");
        itemList.add("Colon Cancer");
        int query35_1 = template1(rules, "HEAD", Integer.toString(1) , itemList,result35_1);
        itemList = new ArrayList<>();
        itemList.add("G72_UP");
        itemList.add("G96_Down");
        int query35_2 = template1(rules, "RULE", Integer.toString(1) , itemList,result35_2);
        //System.out.println("HEAD HAS 1 OF G1_UP...");
        //dislayRules(result35_1);
        //System.out.println("RULE HAS 1 OF (G72_UP, G96_DOWN)");
        //dislayRules(result35_2);
        List<Rule> result35 = new ArrayList<>();
        int query35 = template3(result35_2,"OR",result35_1,result35);
        System.out.println("Number of rules generated from query(HEAD HAS 1 OF DISEASE OR RULE HAS 1 OF (G72_UP, G96_DOWN)) : " + query35);
        //dislayRules(result35);



        // BODY HAS 1 of (G59_UP, G96_DOWN) AND SIZE OF RULE >=3
        List<Rule> result36_1 = new ArrayList<>();
        List<Rule> result36_2 = new ArrayList<>();
        itemList = new ArrayList<>();
        itemList.add("G59_UP");
        itemList.add("G96_Down");
        int query36_1 = template1(rules, "BODY", Integer.toString(1), itemList,result36_1);
        int query36_2 = template2(rules, "RULE", 3, result36_2);
        //System.out.println("BODY HAS 1 of (G59_UP, G96_DOWN)...");
        //dislayRules(result36_1);
        //System.out.println("SIZE OF RULE >=3");
        //dislayRules(result36_2);
        List<Rule> result36 = new ArrayList<>();
        int query36 = template3(result36_1,"AND",result36_2,result36);
        System.out.println("Number of rules generated from query(BODY HAS 1 of (G59_UP, G96_DOWN) AND SIZE OF RULE >=3) : " + query36);
        //dislayRules(result36);

	}

	private static void apriori(List<Transaction> transactions, List<Rule> rules) {

		System.out.println("Support is set to be : " + support);
		List<Itemset> candidateItemsets = new ArrayList<Itemset>();
		List<Itemset> frequentItemsets = new ArrayList<Itemset>();
		frequentItemsets.addAll(candidateItemsets);
		int k = 1,total=0;
		do {
			candidateItemsets = generateCandidateItemset(frequentItemsets, transactions, k);
			frequentItemsets = generateFrequentItemset(candidateItemsets, transactions, rules, k);
			if(frequentItemsets.size()!=0){
				System.out.println("number of length-"+k+" frequent itemset: "+frequentItemsets.size());
				total += frequentItemsets.size();
				}
				else if(frequentItemsets.size() == 0){
					System.out.println("Total:"+total);
					total = 0;
					break;
				}
				k++;
		} while (candidateItemsets.size() > frequentItemsets.size());

	}
	private static void apriori_part1(List<Transaction> transactions,int sup) {

		System.out.println("Support is set to be : " + sup+"%");
		List <Itemset> candidateItemsets = new ArrayList<Itemset>();
		List <Itemset> frequentItemsets = new ArrayList<Itemset>();
		frequentItemsets.addAll(candidateItemsets);
		int k = 1, total = 0;
		do {
			candidateItemsets = generateCandidateItemset(frequentItemsets,transactions,k);
			frequentItemsets = generateFrequentItemset_part1(candidateItemsets, transactions,sup);
			if(frequentItemsets.size()!=0){
			System.out.println("number of length-"+k+" frequent itemset: "+frequentItemsets.size());
			total += frequentItemsets.size();
			}
			else if(frequentItemsets.size() == 0){
				System.out.println("Total:"+total);
				total = 0;
				break;
			}
			k++;
		} while (candidateItemsets.size()>frequentItemsets.size());

	}

	private static List<Itemset> generateCandidateItemset(List<Itemset> candidateItemsets,
			List<Transaction> transactions, int k) {

		if (k == 1) {
			for (Transaction t : transactions) {
				for (String s : t.getTransaction()) {
					Itemset i = new Itemset();
					i.getItems().add(s);
					if (!candidateItemsets.contains(i)) {
						candidateItemsets.add(i);
					}
				}
			}
			// displayItemset(candidateItemsets);
			return candidateItemsets;
		} else {
			List<Itemset> resultItemsets = new ArrayList<Itemset>();
			for (int i = 0; i < candidateItemsets.size(); i++) {
				Set<String> candidateItemSet1 = new HashSet<String>(candidateItemsets.get(i).getItems());
				for (int j = i + 1; j < candidateItemsets.size(); j++) {
					Set<String> candidateItemSet2 = new HashSet<String>(candidateItemsets.get(j).getItems());
					Set<String> union = new HashSet<String>(candidateItemSet1); // use
																				// the
																				// copy
																				// constructor
					union.addAll(candidateItemSet2);
					Itemset item = new Itemset();
					item.setItems(new ArrayList<String>(union));
					if (union.size() == k && !resultItemsets.contains(item)
							&& !candidateItemSet1.equals(candidateItemSet2)) {

						resultItemsets.add(item);
					}
				}
			}
			return resultItemsets;
		}

	}

	private static void displayItemset(List<Itemset> candidateItemsets) {
		for (Itemset i : candidateItemsets) {
			Iterator<String> itr = i.getItems().iterator();
			while (itr.hasNext()) {
				System.out.print(itr.next() + " ");
			}
			System.out.println();
		}
	}

	private static List<Itemset> generateFrequentItemset_part1(List<Itemset> candidateItemsets, List<Transaction> transactions,int sup) {
		
		Map<Itemset,Integer> frequentItemsetMap = new HashMap<Itemset, Integer>();
		
		for (Itemset candidateItem : candidateItemsets) {
			Set<String> candidateItemSet = new HashSet<String>(candidateItem.getItems());
			for(Transaction transaction : transactions) {
				Set<String> transactionSet = new HashSet<String>(transaction.getTransaction());
				if (transactionSet.containsAll(candidateItemSet)) {
					if (frequentItemsetMap.containsKey(candidateItem)) {
						frequentItemsetMap.put(candidateItem, frequentItemsetMap.get(candidateItem)+1);
					} else {
						frequentItemsetMap.put(candidateItem, 1);
					}
				}
			}
		}
		
		// Prune
		List<Itemset> frequentItemset = new ArrayList<Itemset>();
		Set<Itemset> keySet = frequentItemsetMap.keySet();
		Iterator<Itemset> keySetIterator = keySet.iterator();
		int n = sup; // support
		int count = transactions.size();
		while (keySetIterator.hasNext()) {
		   Itemset key = keySetIterator.next();
		   if(frequentItemsetMap.get(key) >= minSupport(n,count)){
			frequentItemset.add(key);   
		   }
		}
		return frequentItemset;
	}
	private static List<Itemset> generateFrequentItemset(List<Itemset> candidateItemsets, List<Transaction> transactions, List<Rule> rules, int k) {

        Map<Itemset, Integer> frequentItemsetMap = new HashMap<Itemset, Integer>();

        for (Itemset candidateItem : candidateItemsets) {
            Set<String> candidateItemSet = new HashSet<String>(candidateItem.getItems());
            for (Transaction transaction : transactions) {
                Set<String> transactionSet = new HashSet<String>(transaction.getTransaction());
                if (transactionSet.containsAll(candidateItemSet)) {
                    if (frequentItemsetMap.containsKey(candidateItem)) {
                        frequentItemsetMap.put(candidateItem, frequentItemsetMap.get(candidateItem) + 1);
                    } else {
                        frequentItemsetMap.put(candidateItem, 1);
                    }
                }
            }
        }

        // Prune
        List<Itemset> frequentItemset = new ArrayList<Itemset>();
        Set<Itemset> keySet = frequentItemsetMap.keySet();
        Iterator<Itemset> keySetIterator = keySet.iterator();
        int n = support; // support
        int count = transactions.size();
        while (keySetIterator.hasNext()) {
            Itemset key = keySetIterator.next();
            if (frequentItemsetMap.get(key) >= minSupport(n, count)) {
                frequentItemset.add(key);
                List<String> its = key.getItems();
                Collections.sort(its);
                String temp = "";
                for(String it: its){
                    temp=temp.concat(it);
                }
                fi_count.put(temp,frequentItemsetMap.get(key));
            }
        }

        // Rule generation
        if (k == 2) {
            for (Itemset item : frequentItemset) {

                Rule rule1 = new Rule();
                String head1 = item.getItems().get(0);
                String body1 = item.getItems().get(1);
                rule1.getHead().add(head1);
                rule1.getBody().add(body1);
                int conf = minConfidence(n,rule1);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule1);
                }

                Rule rule2 = new Rule();
                String head2 = item.getItems().get(1);
                String body2 = item.getItems().get(0);
                rule2.getHead().add(head2);
                rule2.getBody().add(body2);
                conf = minConfidence(n,rule2);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule2);
                }
            }
            //dislayRules(rules);
        }
        if (k == 3) {
            for (Itemset item : frequentItemset) {

                Rule rule1 = new Rule();
                String head1 = item.getItems().get(0);
                String body1 = item.getItems().get(1);
                String body2 = item.getItems().get(2);
                rule1.getHead().add(head1);
                rule1.getBody().add(body1);
                rule1.getBody().add(body2);
                int conf = minConfidence(n,rule1);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule1);
                }
                //rules.add(rule1);

                Rule rule2 = new Rule();
                head1 = item.getItems().get(1);
                body1 = item.getItems().get(0);
                body2 = item.getItems().get(2);
                rule2.getHead().add(head1);
                rule2.getBody().add(body1);
                rule2.getBody().add(body2);
                conf = minConfidence(n,rule2);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule2);
                }
                //rules.add(rule2);

                Rule rule3 = new Rule();
                head1 = item.getItems().get(2);
                body1 = item.getItems().get(0);
                body2 = item.getItems().get(1);
                rule3.getHead().add(head1);
                rule3.getBody().add(body1);
                rule3.getBody().add(body2);
                conf = minConfidence(n,rule3);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule3);
                }
                //rules.add(rule3);

                Rule rule4 = new Rule();
                head1 = item.getItems().get(0);
                String head2 = item.getItems().get(1);
                body2 = item.getItems().get(2);
                rule4.getHead().add(head1);
                rule4.getHead().add(head2);
                rule4.getBody().add(body2);
                conf = minConfidence(n,rule4);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule4);
                }
                //rules.add(rule4);

                Rule rule5 = new Rule();
                head1 = item.getItems().get(1);
                head2 = item.getItems().get(2);
                body2 = item.getItems().get(0);
                rule5.getHead().add(head1);
                rule5.getHead().add(head2);
                rule5.getBody().add(body2);
                conf = minConfidence(n,rule5);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule5);
                }
                //rules.add(rule5);

                Rule rule6 = new Rule();
                head1 = item.getItems().get(0);
                head2 = item.getItems().get(2);
                body2 = item.getItems().get(1);
                rule6.getHead().add(head1);
                rule6.getHead().add(head2);
                rule6.getBody().add(body2);
                conf = minConfidence(n,rule6);
                //System.out.println("confidence: " + conf);
                if(conf>=confidence){
                    rules.add(rule6);
                }
                //rules.add(rule6);

            }
            dislayRules(rules);

        }

        return frequentItemset;
    }

	private static void dislayRules(List<Rule> rules) {

		for (Rule rule : rules) {
			System.out.println("{" + rule.getBody() + "}->{" + rule.getHead() + "}");
		}

	}

	public static int minSupport(int n, int count) {
		return (int) (n * count / 100);
	}

	public static int minConfidence(int n, Rule r) {
        //conf = count(head) + count(body)/count(body)
        List<String> body = r.getBody();
        List<String> head = r.getHead();
        List<String> all = new ArrayList<String>();
        all.addAll(body);
        all.addAll(head);
        Collections.sort(all);
        String str_a = new String();
        for(String a:all){
            str_a=str_a.concat(a);
        }
        int cnt_all = fi_count.get(str_a);
        Collections.sort(body);
        String str_b = new String();
        for(String b:body){
            str_b=str_b.concat(b);
        }
        int cnt_body = fi_count.get(str_b);
        return (int) (100 * cnt_all/cnt_body);
    }

	public static void parse(List<Transaction> transactions) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String sCurrentLine;
			String[] term;
			while ((sCurrentLine = br.readLine()) != null) {
				Transaction t = new Transaction();
				term = sCurrentLine.split("\t");
				for (int i = 1; i < term.length; i++) {
					if (term[i].equalsIgnoreCase("UP") || term[i].equalsIgnoreCase("Down")) {
						t.getTransaction().add("G" + Integer.toString(i) + "_" + term[i]);
					} else {
						t.getTransaction().add(term[i].toString());
					}
				}
				transactions.add(t);
			}
			// displayTransactions(transactions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void displayTransactions(List<Transaction> transactions) {

		for (Transaction t : transactions) {
			Iterator<String> itr = t.getTransaction().iterator();
			while (itr.hasNext()) {
				System.out.print(itr.next() + " ");
			}
			System.out.println();
		}
	}

	public static int template1(List<Rule> rules, String RBH, String HAS, List<String> items, List<Rule> resultSet) {
		int count = 0;

		if (RBH.equalsIgnoreCase("RULE")) {
			for (Rule rule : rules) {
				if (HAS.equalsIgnoreCase("ANY")) {
					for (String item : items) {
						if (rule.getHead().contains(item) || rule.getBody().contains(item)) {
							count++;
							resultSet.add(rule);
							break;
						}
					}
				} else if (HAS.equalsIgnoreCase("NONE")) {
					for (String item : items) {
						if (!rule.getHead().contains(item) && !rule.getBody().contains(item)) {
							count++;
							resultSet.add(rule);
							break;
						}
					}
				} else {
					if (Integer.parseInt(HAS) > 0) {
						int reference_count = Integer.parseInt(HAS);
						int cnt = 0;
						for (String item : items) {
							if (rule.getHead().contains(item) || rule.getBody().contains(item)) {
								cnt++;
							}
						}
						if (cnt == reference_count) {
							resultSet.add(rule);
							count++;
						}
					}
				}
			}
		} else if (RBH.equalsIgnoreCase("HEAD")) {
			for (Rule rule : rules) {
				Set<String> Head = new HashSet<String>(rule.getHead());
				if (HAS.equalsIgnoreCase("ANY")) {
					for (String item : items) {
						if (rule.getHead().contains(item)) {
							count++;
							resultSet.add(rule);
							break;
						}
					}
				} else if (HAS.equalsIgnoreCase("NONE")) {
					boolean flag = false;
					for (String item : items) {
						flag = rule.getHead().contains(item);
						if (flag) {
							break;
						}
					}
					if (!flag) {
						count++;
						resultSet.add(rule);
					}
				} else {
					if (Integer.parseInt(HAS) > 0) {
						int reference_count = Integer.parseInt(HAS);
						int cnt = 0;
						for (String item : items) {
							if (rule.getHead().contains(item)) {
								cnt++;
							}
						}
						if (cnt == reference_count) {
							resultSet.add(rule);
							count++;
						}
					}
				}
			}
		} else if (RBH.equalsIgnoreCase("BODY")) {
			for (Rule rule : rules) {
				Set<String> body = new HashSet<String>(rule.getBody());
				// similar if and else ladder as generaed in
				// {if(RBH.equalsIgnoreCase("RULE")}
				if (HAS.equalsIgnoreCase("ANY")) {
					for (String item : items) {
						if (rule.getBody().contains(item)) {
							count++;
							resultSet.add(rule);
							break;
						}
					}
				} else if (HAS.equalsIgnoreCase("NONE")) {
					for (String item : items) {
						if (!rule.getBody().contains(item)) {
							count++;
							resultSet.add(rule);
							break;
						}
					}
				} else {
					if (Integer.parseInt(HAS) > 0) {
						int reference_count = Integer.parseInt(HAS);
						int cnt = 0;
						for (String item : items) {
							if (rule.getBody().contains(item)) {
								cnt++;
							}
						}
						if (cnt == reference_count) {
							resultSet.add(rule);
							count++;
						}
					}
				}
			}
		}

		return count;
	}

	public static int template2(List<Rule> rules, String RBH, int num, List<Rule> result) {
		int count = 0;
		// List<Rule> result = new ArrayList<>();
		for (Rule rule : rules) {
			int size_body = rule.getBody().size();
			int size_head = rule.getHead().size();
			int size_rule = size_body + size_head;

			if (RBH.equalsIgnoreCase("RULE")) {
				if (size_rule >= num) {
					result.add(rule);
					count++;
				}
			} else if (RBH.equalsIgnoreCase("HEAD")) {
				if (size_head >= num) {
					result.add(rule);
					count++;
				}
			} else if (RBH.equalsIgnoreCase("BODY")) {
				if (size_body >= num) {
					result.add(rule);
					count++;
				}
			}
		}
		return count;
	}

	public static int template3(List<Rule> rules1, String operator, List<Rule> rules2, List<Rule> result) {
        int count = 0;
        //List<Rule> result = new ArrayList<>();
        List<Rule> resultset1 = rules1;
        List<Rule> resultset2 = rules2;
        if(operator.toLowerCase().equalsIgnoreCase("and")) {
            for (Rule r1 : resultset1) {
                for (Rule r2 : resultset2) {
                    if ((r1.getHead().size() == r2.getHead().size()) && (r1.getBody().size() == r2.getBody().size())) {
                        Boolean flag = true;
                        for (String r_h1 : r1.getHead()) {
                            flag = flag && r2.getHead().contains(r_h1);
                        }
                        if (flag) {
                            for (String r_b1 : r1.getBody()) {
                                flag = flag && r2.getBody().contains(r_b1);
                            }
                            if (flag) {
                                count++;
                                result.add(r1);
                            }
                        } 
                    }
                }
            }
        }
        else if (operator.toLowerCase().equalsIgnoreCase("or")){
            HashSet<Rule> or_result= new HashSet<>();
            for (Rule r1 : resultset1) {
                or_result.add(r1);
                for (Rule r2 : resultset2) {
                    or_result.add(r2);
                }
            }
            count=or_result.size();
            Iterator<Rule> it = or_result.iterator();
            while(it.hasNext()){
                result.add(it.next());
            }
        }
        return count;
    }
}
