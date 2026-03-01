package org.models;

import org.storage.DocumentStore;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Hændelse(int hændelseId, String navn, Instant valørTid, Instant realTid,
                       List<BeregningsGeneration> generationer) {
    public Hændelse(String navn, Instant valørTid, Instant realTid) {
        this(DocumentStore.GlobalId++, navn, valørTid, realTid, new ArrayList<>());
    }

    public Tilstand Beregn(Tilstand old, HændelseInput input) {
        if (navn.equals("indbetaling")) {
            var month = valørTid.atZone(ZoneId.systemDefault()).getMonthValue();

            var resultat = new Tilstand(new HashMap<>(old.IndbetalingerPrÅr()));
            var sum = resultat.IndbetalingerPrÅr().getOrDefault(month, 0);

            sum = sum + (int) input.inputs().get("beløb");
            resultat.IndbetalingerPrÅr().put(month, sum);

            generationer.add(new BeregningsGeneration(input, resultat));
            return resultat;
        }

        throw new RuntimeException("Ukendt hændelse " + navn);
    }
}
