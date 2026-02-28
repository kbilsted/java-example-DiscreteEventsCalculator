package org.example;

import java.time.Instant;
import java.util.List;

public record Hændelse(int hændelseId, String navn, Instant valørTid, Instant realTid, List<BeregningsGeneration> Generationer)  {
    public  Hændelse (String navn, Instant valørTid, Instant realTid){
        this(DocumentStore.GlobalId++, navn, valørTid, realTid, List.of( ));
    }
}

