package com.github.xabgesagtx.mensa.web.cache;

import com.github.xabgesagtx.mensa.config.LabelCacheConfig;
import com.github.xabgesagtx.mensa.model.Label;
import com.github.xabgesagtx.mensa.persistence.LabelRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class LabelCache {

    @Autowired
    private LabelRepository repo;

    @Autowired
    private LabelCacheConfig config;

    private final List<Label> labels = new ArrayList<>();

    public Optional<Label> getLabel(String searchText) {
        String simplifiedSearchText = simplify(searchText);
        synchronized (labels) {
            return labels.stream().filter(label -> isLabel(label,simplifiedSearchText)).findFirst();
        }
    }

    private boolean isLabel(Label label, String simplifiedSearchText) {
        String simplifiedLabelName = simplify(label.getName());
        return simplifiedLabelName.equals(simplifiedSearchText) || StringUtils.startsWith(simplifiedLabelName, simplifiedSearchText) || StringUtils.startsWith(simplifiedSearchText, simplifiedLabelName);
    }

    private String simplify(String text) {
        return removeStopWords(StringUtils.defaultString(text).toLowerCase(Locale.GERMANY)).replaceAll("\\s", StringUtils.EMPTY);
    }

    private String removeStopWords(String text) {
        String cleanText = text;
        for (String stopword : config.getStopwords()) {
            cleanText = StringUtils.removeStart(cleanText, stopword + " ");
        }
        return cleanText;
    }

    @Scheduled(cron = "${cache.labels.cron}")
    public void update() {
        List<Label> newLabels = repo.findAll();
        synchronized (labels) {
            labels.clear();
            labels.addAll(newLabels);
        }
    }

    @PostConstruct
    public void start() {
        update();
    }

}
