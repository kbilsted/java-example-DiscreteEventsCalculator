package org.example;

import java.time.Instant;
import java.util.HashMap;

public  record  HændelseInput(int inputId, Instant realTid, HashMap<String, Object> inputs){}
