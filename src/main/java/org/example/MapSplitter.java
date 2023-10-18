package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapSplitter {

    public void splitAndPrintMap(Map<String, String> inputMap, int numThreads) {
        // Разделение Map на части
        List<Map<String, String>> mapParts = new ArrayList<>();
        int chunkSize = inputMap.size() / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int startIndex = i * chunkSize;
            int endIndex = (i == numThreads - 1) ? inputMap.size() : (i + 1) * chunkSize;
            Map<String, String> chunkMap = new HashMap<>();

            int counter = 0;
            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                if (counter >= startIndex && counter < endIndex) {
                    chunkMap.put(entry.getKey(), entry.getValue());
                }
                counter++;
            }

            mapParts.add(chunkMap);
        }

        // Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                Map<String, String> chunkMap = mapParts.get(threadIndex);
                Gs1Check gs1 = new Gs1Check();
                gs1.Start(chunkMap);
            });
        }

        // Завершаем работу пула потоков и ждем завершения
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Пул потоков не завершил работу в течение 60 секунд.");
            }
        } catch (InterruptedException e) {
            System.err.println("Произошла ошибка при ожидании завершения потоков: " + e.getMessage());
        }
    }
}
