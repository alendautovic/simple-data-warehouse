package com.github.alendautovic.datawarehouse.config.seed;

import com.github.alendautovic.datawarehouse.domain.DailyStatistics;
import com.github.alendautovic.datawarehouse.repository.DailyStatisticsRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CsvDataLoader implements ApplicationRunner {

    private final DailyStatisticsRepository dailyStatisticsRepository;

    public CsvDataLoader(
            DailyStatisticsRepository dailyStatisticsRepository) {
        this.dailyStatisticsRepository = dailyStatisticsRepository;
    }

    /**
     * Method that loads daily statistics data from csv file, only once. Automatically called after application start (see {@link ApplicationRunner}).
     * Csv file is named "data.csv" and is located in resources folder
     *
     * @param args {@link ApplicationArguments}
     * @throws IOException in case of I/O error while reading data from csv file
     */
    @Override
    public void run(ApplicationArguments args) throws IOException {

        if (dailyStatisticsRepository.count() != 0) {
            return;
        }

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("data.csv");
        if (inputStream == null) return;

        Reader reader = new InputStreamReader(inputStream);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines(true)
                .withDelimiter(',')
                .withTrim()
                .parse(reader);

        List<DailyStatistics> dailyStatisticsList = StreamSupport
                .stream(records.spliterator(), false)
                .map(csvRecord -> {
                    DailyStatistics dailyStatistics = new DailyStatistics();
                    dailyStatistics.setDatasource(csvRecord.get(0));
                    dailyStatistics.setCampaign(csvRecord.get(1));
                    dailyStatistics.setDate(LocalDate.parse(csvRecord.get(2), formatter));
                    dailyStatistics.setClicks(Integer.parseInt(csvRecord.get(3)));
                    dailyStatistics.setImpressions(Integer.parseInt(csvRecord.get(4)));
                    return dailyStatistics;
                })
                .collect(Collectors.toList());

        dailyStatisticsRepository.saveAll(dailyStatisticsList);
    }
}