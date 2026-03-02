package org.models;

import java.util.HashMap;

/** the accumulated state of a time-line calculation */
public record State(HashMap<Integer, Integer> paymentsPerYear) {
}
