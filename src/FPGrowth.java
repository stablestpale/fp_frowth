import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author zzy
 * @description FP-Growth
 * @date 2022/4/14 22:05
 */


class FPGrowth {
    private Node root;
    private Integer minAppear;
    private Map<Integer, Set<List<String>>> frequencyItemSet;

    FPGrowth(Integer minAppear, String fileName) {
        this.minAppear = minAppear;
        this.frequencyItemSet = new HashMap<>();
        this.root = new Node();
        List<Row> table = new ArrayList<>();
        Map<String, Node> headPointers = new HashMap<>();
        // ����
        read(fileName, table);
        // ����FP-Tree
        createTPTree(table, headPointers, root);
        // ��ȡƵ���
        createFrequentItemSet(headPointers, new ArrayList<>());
    }


    void print() {
        // �ݹ��ӡFP Tree
        System.out.println("FP-TREE:");
        printFPTree(root, 0);

        // ��ӡƵ���
        System.out.println("\n\nƵ���:");
        List<Integer> keys = new ArrayList<>(frequencyItemSet.keySet());
        Collections.sort(keys);
        int count = 0;
        for(Integer key: keys) {
            count += frequencyItemSet.get(key).size();
            if(key == 1) continue;
            System.out.printf("Ƶ��%d�:  ", key);
            System.out.println(frequencyItemSet.get(key));
        }
        System.out.println("��" + count + "��");
    }

    private void printFPTree(Node node, int depth) {
        for(int i = 0; i < depth; ++i) {
            System.out.print("    ");
        }
        if(node.getItem() != null) {
            System.out.println("|__  " + node.getItem() + ":" + node.getCount());
        } else {
            System.out.println("null");
        }
        for(Node child: node.getChildren().values()) {
            printFPTree(child, depth + 1);
        }
    }

    /*
     * @description: �������ݣ�����table��
     * @param: [fileName, table]
     * @return: void
     * @date: 0:17 2022/4/15
     */
    private void read(String fileName, List<Row> table) {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while(scanner.hasNext()) {
                Row row = new Row();
                row.setData(new ArrayList<>(Arrays.asList(scanner.nextLine().split(" "))));
                table.add(row);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * @description: FPTree�е���ָ���������޳���������С֧�ֶȵ���;ɾ��table�в����ϵ���Ŀ��������
     * @param: [table, headPointers]
     * @return: void
     * @date: 0:17 2022/4/15
     */
    private void createHeadPointersAndSort(List<Row> table, Map<String, Node> headPointers) {
        // FPTree�е���ָ������
        for(Row row: table) {
            for(String item: row.getData()) {
                if(headPointers.containsKey(item)) {
                    headPointers.get(item).increase(row.getCount());
                } else {
                    headPointers.put(item, new Node(row.getCount()));
                }
            }
        }
        // �޳���������С֧�ֶȵ���
        headPointers.values().removeIf(value -> value.getCount() < minAppear);

        // ɾ��table�в����ϵ�item��������
        for(Row row: table) {
            row.getData().removeIf(item -> !headPointers.containsKey(item));
            row.getData().sort((a, b) -> {
                if(headPointers.get(b).getCount() < headPointers.get(a).getCount()) {
                    return -1;
                } else if(headPointers.get(b).getCount() > headPointers.get(a).getCount()) {
                    return 1;
                } else {
                    return a.compareTo(b);
                }
            });
        }
    }

    /*
     * @description: ����TP-Tree
     * @param: [table, headPointers, root]
     * @return: void
     * @date: 0:16 2022/4/15
     */
    private void createTPTree(List<Row> table, Map<String, Node> headPointers, Node root) {
        // ����head����
        createHeadPointersAndSort(table, headPointers);
        for(Row row: table) {
            Node current = root; // ��������ʼ����
            for(String item: row.getData()) {
                Node child = current.getChildren().get(item);
                if(child == null) {
                    child = new Node(item, current);
                    // ����ǰ�ڵ����FP-Tree��ǰһ�ڵ���ӽڵ�
                    current.getChildren().put(item, child);
                    // ����ǰ�ڵ�����Ӧitem��head������
                    headPointers.get(item).add(child);
                }
                child.increase(row.getCount());
                current = child;
            }
        }
    }

    /*
     * @description: ����F-List
     * @param: [headPointers]
     * @return: List<Entry<String,Node>>
     * @date: 0:28 2022/4/15
     */
    private List<Map.Entry<String, Node>> createFList(Map<String, Node> headPointers) {
        // ��ȡ�����F-List
        List<Map.Entry<String, Node>> fList = new ArrayList<>(headPointers.entrySet());
        fList.sort((a, b) -> {
            if(a.getValue().getCount() < b.getValue().getCount()) {
                return -1;
            } else if(a.getValue().getCount() > b.getValue().getCount()) {
                return 1;
            } else {
                return b.getKey().compareTo(a.getKey());
            }
        });
        return fList;
    }

    /*
     * @description: �ݹ�����Ƶ���
     * @param: [headPointers, frequencyList]
     * @return: void
     * @date: 1:30 2022/4/15
     */
    private void createFrequentItemSet(Map<String, Node> headPointers, List<String> frequencyList) {
        // ����F-List
        List<Map.Entry<String, Node>> fList = createFList(headPointers);
        for(Map.Entry<String, Node> entry: fList) {
            // ��������뵽��ǰ���Ӧ��Ƶ�����
            frequencyList.add(entry.getKey());
            Set<List<String>> set = frequencyItemSet.getOrDefault(frequencyList.size(), new HashSet<>());
            set.add(new ArrayList<>(frequencyList));
            frequencyItemSet.put(frequencyList.size(), set);
            // ��ȡ����ģʽ��
            List<Row> list = getConditionalPatternBases(entry.getValue());
            // ������ģʽ������TP Tree
            Map<String, Node> newHeadPointers = new HashMap<>();
            Node root = new Node();
            createTPTree(list, newHeadPointers, root);
            createFrequentItemSet(newHeadPointers, frequencyList);
            // ����ݹ������ɾ������
            frequencyList.remove(frequencyList.size() - 1);
        }
    }

    /*
     * @description: ��ȡ����ģʽ��
     * @param: [dummy]
     * @return: List<Row>
     * @date: 1:10 2022/4/15
     */
    private List<Row> getConditionalPatternBases(Node dummy) {
        List<Row> list = new ArrayList<>();
        // ����ͬitem����
        Node currentNode = dummy.getHead();
        while(currentNode != null) {
            // ����TP-Tree�����׽ڵ㵽currentNode��·��
            List<String> data = new ArrayList<>();
            Node lastNode = currentNode.getParent();
            while(lastNode.getItem() != null) {
                data.add(lastNode.getItem());
                lastNode = lastNode.getParent();
            }
            Row row = new Row(currentNode.getCount(), data);
            list.add(row);
            currentNode = currentNode.getNext();
        }
        return list;
    }
}
