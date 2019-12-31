package com.llevett;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;


public final class WordUtil {
	private WordUtil() {
		throw new AssertionError("Attempt to instantiate WordCount class");
	}

	public static Map<String, Integer> countWords(final String phrase) {
        return countWordsInto(phrase, new ConcurrentSkipListMap<String, Integer>());
	}

	public static Map<String, Integer> countOccurrences(final String phrase, final String[] targets) {
		final ConcurrentSkipListMap<String, Integer> preFilledMap =
				Stream.of(targets)
					.parallel()
					.collect(Collectors.toConcurrentMap(
							Function.identity(), found -> 0, (left, right) -> 0, ConcurrentSkipListMap::new));

		return countWordsInto(phrase, preFilledMap);
	}

	private static Map<String, Integer> countWordsInto(final String phrase, ConcurrentSkipListMap<String, Integer> preFilledMap) {
		return Stream.of(phrase.split("\\s"))
        		.parallel()
        		.map(word -> word.toLowerCase())
        		.map(word -> word.replaceAll("[^a-zA-Z]", ""))
        		.collect(Collectors.toConcurrentMap(
        				Function.identity(), found -> 1, Integer::sum, () -> preFilledMap));
	}

	public static Map<String, Set<String>> groupByFirstLetter(final String phrase) {
		return Stream.of(phrase.split("\\s"))
        		.parallel()
        		.map(word -> word.toLowerCase())
        		.map(word -> word.replaceAll("[^a-zA-Z]", ""))
        		.collect(Collectors.toConcurrentMap(
        				word -> word.substring(0, 1), word -> Set.of(word), SetUtils::union, ConcurrentSkipListMap::new));
	}
}
