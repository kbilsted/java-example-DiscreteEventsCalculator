package org.models;

import org.storage.GlobalId;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Hændelse(int hændelseId, String navn, Instant valørTid, Instant realTid,
                       List<BeregningsGeneration> generationer) {
    public Hændelse(String navn, Instant valørTid, Instant realTid) {
        this(GlobalId.next(), navn, valørTid, realTid, new ArrayList<>());
    }

    public Tilstand Beregn(Tilstand old, HændelseInput input) {
        if (navn.equals("indbetaling")) {
            var year = valørTid.atZone(ZoneId.systemDefault()).getYear();

            var resultat = new Tilstand(new HashMap<>(old.IndbetalingerPrÅr()));
            var sum = resultat.IndbetalingerPrÅr().getOrDefault(year, 0);

            sum = sum + (int) input.inputs().get("beløb");
            resultat.IndbetalingerPrÅr().put(year, sum);

            generationer.add(new BeregningsGeneration(input, resultat));
            return resultat;
        }

        throw new RuntimeException("Ukendt hændelse " + navn);
    }
}
