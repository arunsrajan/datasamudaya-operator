package com.github.datasamudaya.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;

import io.javaoperatorsdk.operator.Operator;


public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) throws Exception {
        Operator operator = new Operator();
        operator.register(new DatasamudayaOperatorReconciler());
        operator.start();
        new FtBasic(new TkFork(new FkRegex("/health", "ALL GOOD!")), 8080).start(Exit.NEVER);
        log.info("Operator started.");
    }
}
