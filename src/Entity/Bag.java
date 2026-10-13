package Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Bag {
    private List<Item> itemList;
    private Map<Item, Integer> numItemMap;
    private int countItem;

    public Bag(){
        itemList = new ArrayList<>();
        numItemMap = new HashMap<>();
        countItem = 0;
    }

    public List<Item> getItemList() {
        return new ArrayList<>(itemList);
    }

    public Integer getNumItemMap(Item item) {
        return numItemMap.getOrDefault(item, 0);
    }

    public void addItem(Item item, Integer num) {
        Objects.requireNonNull(item, "item");
        if (num == null || num <= 0) throw new IllegalArgumentException("Quantity must be positive");
        // If the item is already in the itemList, just add the num to the
        // current num in numItemMap
        if (itemList.contains(item)) {
            numItemMap.put(item, numItemMap.get(item) + num);
        } else {
            itemList.add(item);
            numItemMap.put(item, num);
            countItem++;
        }
    }

    public Item getItem(int index) {
        return itemList.get(index);
    }

    public int getCountItem() {
        return countItem;
    }
    // TODO: Implement getter methods and refactor the itemList data structure
}
