package com.company;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        String[] routes = new String[1000];
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < routes.length; i++) {
            routes[i] = generateRoute("RLRFR", 100);
        }

        int ind = 0;
        for (String route : routes) {
            threads.add(new Thread(() -> {
                int countR = 0;
                for (int i = 0; i < route.length(); i++) {
                    if (route.charAt(i) == 'R') {
                        ++countR;
                    }
                }
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.isEmpty()) {
                        if (sizeToFreq.containsKey(countR)) {
                            sizeToFreq.merge(countR, 1, (x, y) -> x + y);
                        } else
                            sizeToFreq.put(countR, 1);
                    } else
                        sizeToFreq.put(countR, 1);

                }
                System.out.println(" количество команд поворота направо в маршруте -> " + countR);
            }));
            threads.get(ind).start();
            ++ind;
        }

        for (Thread thread : threads) {
            thread.join();
        }

        new Thread(() -> {
            synchronized (sizeToFreq) {
                Object maxVal = Collections.max(sizeToFreq.values());
                Optional<Integer> result = sizeToFreq.entrySet()
                        .stream()
                        .filter(entry -> maxVal.equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst();

                if (result.isPresent()) {
                    System.out.println("Самое частое количество повторений " + result.get() +
                            " (встретилось " + maxVal + " раз)");
                }
                sizeToFreq.remove(result.get());
                System.out.println("Другие размеры:");
                for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                    System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
                }

            }
        }).start();

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
