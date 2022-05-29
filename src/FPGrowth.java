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
        // 读入
        read(fileName, table);
        // 构造FP-Tree
        createTPTree(table, headPointers, root);
        // 获取频繁项集
        createFrequentItemSet(headPointers, new ArrayList<>());
    }


    void print() {
        // 递归打印FP Tree
        System.out.println("FP-TREE:");
        printFPTree(root, 0);

        // 打印频繁项集
        System.out.println("\n\n频繁项集:");
        List<Integer> keys = new ArrayList<>(frequencyItemSet.keySet());
        Collections.sort(keys);
        int count = 0;
        for(Integer key: keys) {
            count += frequencyItemSet.get(key).size();
            if(key == 1) continue;
            System.out.printf("频繁%d项集:  ", key);
            System.out.println(frequencyItemSet.get(key));
        }
        System.out.println("共" + count + "项");
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
     * @description: 读入数据，存入table中
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
     * @description: FPTree中的首指针链表，并剔除不符合最小支持度的项;删除table中不符合的项目，并排序
     * @param: [table, headPointers]
     * @return: void
     * @date: 0:17 2022/4/15
     */
    private void createHeadPointersAndSort(List<Row> table, Map<String, Node> headPointers) {
        // FPTree中的首指针链表
        for(Row row: table) {
            for(String item: row.getData()) {
                if(headPointers.containsKey(item)) {
                    headPointers.get(item).increase(row.getCount());
                } else {
                    headPointers.put(item, new Node(row.getCount()));
                }
            }
        }
        // 剔除不符合最小支持度的项
        headPointers.values().removeIf(value -> value.getCount() < minAppear);

        // 删除table中不符合的item，并排序
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
     * @description: 构造TP-Tree
     * @param: [table, headPointers, root]
     * @return: void
     * @date: 0:16 2022/4/15
     */
    private void createTPTree(List<Row> table, Map<String, Node> headPointers, Node root) {
        // 构造head链表
        createHeadPointersAndSort(table, headPointers);
        for(Row row: table) {
            Node current = root; // 由树根开始创建
            for(String item: row.getData()) {
                Node child = current.getChildren().get(item);
                if(child == null) {
                    child = new Node(item, current);
                    // 将当前节点加入FP-Tree中前一节点的子节点
                    current.getChildren().put(item, child);
                    // 将当前节点加入对应item的head链表中
                    headPointers.get(item).add(child);
                }
                child.increase(row.getCount());
                current = child;
            }
        }
    }

    /*
     * @description: 构造F-List
     * @param: [headPointers]
     * @return: List<Entry<String,Node>>
     * @date: 0:28 2022/4/15
     */
    private List<Map.Entry<String, Node>> createFList(Map<String, Node> headPointers) {
        // 获取有序的F-List
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
     * @description: 递归生成频繁项集
     * @param: [headPointers, frequencyList]
     * @return: void
     * @date: 1:30 2022/4/15
     */
    private void createFrequentItemSet(Map<String, Node> headPointers, List<String> frequencyList) {
        // 构造F-List
        List<Map.Entry<String, Node>> fList = createFList(headPointers);
        for(Map.Entry<String, Node> entry: fList) {
            // 将自身加入到当前项对应的频繁项集中
            frequencyList.add(entry.getKey());
            Set<List<String>> set = frequencyItemSet.getOrDefault(frequencyList.size(), new HashSet<>());
            set.add(new ArrayList<>(frequencyList));
            frequencyItemSet.put(frequencyList.size(), set);
            // 获取条件模式基
            List<Row> list = getConditionalPatternBases(entry.getValue());
            // 用条件模式基制作TP Tree
            Map<String, Node> newHeadPointers = new HashMap<>();
            Node root = new Node();
            createTPTree(list, newHeadPointers, root);
            createFrequentItemSet(newHeadPointers, frequencyList);
            // 本项递归结束后删除自身
            frequencyList.remove(frequencyList.size() - 1);
        }
    }

    /*
     * @description: 获取条件模式基
     * @param: [dummy]
     * @return: List<Row>
     * @date: 1:10 2022/4/15
     */
    private List<Row> getConditionalPatternBases(Node dummy) {
        List<Row> list = new ArrayList<>();
        // 遍历同item链表
        Node currentNode = dummy.getHead();
        while(currentNode != null) {
            // 依靠TP-Tree回溯首节点到currentNode的路径
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
