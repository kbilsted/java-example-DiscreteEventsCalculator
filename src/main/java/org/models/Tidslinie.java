package org.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Tidslinie(List<Hændelse> hændelser) {
    public Tidslinie() {
        this(new ArrayList<>());
    }

    public void Add(Hændelse hændelse, HændelseInput input) {
        int pos = 0;
        while (pos < hændelser.size() && hændelser.get(pos).valørTid().isAfter(hændelse.valørTid()))
            pos++;
        hændelser.add(pos, hændelse);

        // beregn hændelse
        Tilstand state = pos == 0
                ? new Tilstand(new HashMap<>())
                : hændelser().get(pos - 1).generationer().getLast().tilstand();

        state = hændelse.Beregn(state, input);

        // genberegn resten af tidslinien
        for (pos = pos + 1; pos < hændelser.size(); pos++) {
            var event = hændelser.get(pos);
            var calcInput = event.generationer().getLast().input();
            state = hændelser.get(pos).Beregn(state, calcInput);
        }
    }

}
