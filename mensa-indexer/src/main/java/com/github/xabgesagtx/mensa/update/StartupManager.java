package com.github.xabgesagtx.mensa.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

import static reactor.bus.selector.Selectors.$;

@Component
public class StartupManager {

    @Autowired
    private MensaUpdater mensaUpdater;

    @Autowired
    private AllergenUpdater allergenUpdater;

    @Autowired
    private LabelUpdater labelUpdater;

    @Autowired
    private AllMenuUpdateStarter menuUpdateStarter;

    @Autowired
    private DayUpdater dayUpdater;

    @Autowired
    private MensaMenuUpdater menuUpdater;

    @Autowired
    private EventBus bus;

    @PostConstruct
    public void start() {
        registerConsumers();
        startUpdates();
    }

    private void startUpdates() {
        mensaUpdater.update();
        allergenUpdater.update();
        labelUpdater.update();
        menuUpdateStarter.startUpdates();
    }

    private void registerConsumers() {
        bus.on($("mensa_menu_update"), menuUpdater);
        bus.on($("day_update"), dayUpdater);
    }
}
