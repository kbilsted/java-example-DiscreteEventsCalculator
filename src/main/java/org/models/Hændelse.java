package org.models;

import org.storage.DocumentStore;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

public record Hændelse(int hændelseId, String navn, Instant valørTid, Instant realTid, List<BeregningsGeneration> generationer)  {
    public  Hændelse (String navn, Instant valørTid, Instant realTid){
        this(DocumentStore.GlobalId++, navn, valørTid, realTid, new ArrayList<>());
    }

    public void AddInput(HændelseInput input){
        generationer.add(new BeregningsGeneration(input, null));

    }
}

