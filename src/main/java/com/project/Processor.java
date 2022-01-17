package com.project;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Processor {
    private static final String SPLIT_REGEX = ",";

    private final TreeMap<Integer, Integer> bid = new TreeMap();
    private final TreeMap<Integer, Integer> ask = new TreeMap();

    public void process(Supplier<String> source, Consumer<String> drain) {
        while (true) {
            String line = source.get();
            if (Objects.isNull(line)) {
                break;
            }
            if(line.length() == 0){
                break;
            }
            char commandType = line.charAt(0);
            String[] splitLine = line.split(SPLIT_REGEX);
            switch (commandType) {
                case 'q':
                    drain.accept(queryProcess(splitLine));
                    break;
                case 'u':
                    updateProcess(splitLine);
                    break;
                case 'o':
                    orderProcess(splitLine);
                    break;
            }
        }
    }

    //u,11,5,ask
    private void updateProcess(String[] splitLine) {
        int price = Integer.parseInt(splitLine[1]);
        int size = Integer.parseInt(splitLine[2]);
        String command = splitLine[3];
        if ("bid".equals(command)) {
            if (size == 0) {
                bid.remove(price);
            } else {
                bid.put(price, size);
            }
        } else if ("ask".equals(command)) {
            if (size == 0) {
                ask.remove(price);
            } else {
                ask.put(price, size);
            }
        }
    }

    //q,best_bid
    private String queryProcess(String[] splitLine) {
        StringBuilder sb = new StringBuilder();
        String command = splitLine[1];
        switch (command) {
            case "best_bid":
                int lastKey = bid.lastKey();
                sb.append(lastKey)
                        .append(",")
                        .append(bid.getOrDefault(lastKey, 0));
                break;
            case "best_ask":
                int firstKey = ask.lastKey();
                sb.append(firstKey)
                        .append(",")
                        .append(ask.getOrDefault(firstKey, 0));
                break;
            case "size":
                int price = Integer.parseInt(splitLine[2]);
                sb.append(bid.getOrDefault(price, 0) + ask.getOrDefault(price, 0));
                break;
        }
        return sb.toString();
    }

    private void orderProcess(String[] splitLine) {
        String command = splitLine[1];
        if ("sell".equals(command)) {
            sell(splitLine);
        } else if ("buy".equals(command)) {
            buy(splitLine);
        }
    }

    //o,sell,1
    private void sell(String[] parts) {
        int sizeShares = Integer.parseInt(parts[2]);
        while (sizeShares > 0) {
            Map.Entry<Integer, Integer> lastEntry = bid.lastEntry();
            int lastKey = lastEntry.getKey();
            if (lastEntry.getValue() <= sizeShares) {
                sizeShares -= lastEntry.getValue() ;
                bid.remove(lastKey);
            } else {
                bid.put(lastKey, lastEntry.getValue() - sizeShares);
                sizeShares = 0;
            }
        }
    }


    private void buy(String[] parts) {
        int sizeShares = Integer.parseInt(parts[2]);
        while (sizeShares > 0) {
            Map.Entry<Integer, Integer> firstEntry = ask.firstEntry();
            int firstKey = firstEntry.getKey();
            if (firstEntry.getValue() <= sizeShares) {
                sizeShares -= firstEntry.getValue() ;
                ask.remove(firstKey);
            } else {
                ask.put(firstKey, firstEntry.getValue() - sizeShares);
                sizeShares = 0;
            }
        }
    }

}
