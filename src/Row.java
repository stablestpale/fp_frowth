import java.util.ArrayList;
import java.util.List;

/**
 * @author zzy
 * @description
 * @date 2022/4/14 22:09
 */


class Row {
    private Integer count;
    private List<String> data;

    Row() {
        this.count = 1;
    }

    Row(Integer count, List<String> data) {
        this.count = count;
        this.data = data;
    }

    List<String> getData() {
        return data;
    }

    void setData(List<String> data) {
        this.data = data;
    }

    Integer getCount() {
        return count;
    }

    void setCount(Integer count) {
        this.count = count;
    }
}
