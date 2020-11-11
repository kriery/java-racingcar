package race.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RaceTest {
    private List<String> carNames;

    @BeforeEach
    void setCarNames() {
        carNames = Arrays.asList("petty", "loopy", "crong", "poby", "harry");
    }

    @DisplayName("test for cars' initial position")
    @Test
    void initialPosition() {
        List<Car> cars = new Race(EngineFactory.BROKEN_ENGINE_FACTORY, carNames).getCars();
        cars.forEach(car -> assertThat(car.getCurrentPosition()).isEqualTo(0));
    }

    @DisplayName("test for each lap's position with perfect engine")
    @RepeatedTest(10)
    void lapWithPerfectEngine() {
        final int iterations = 20;
        Race race = new Race(EngineFactory.PERFECT_ENGINE_FACTORY, carNames);
        IntStream.range(1, iterations).forEach(i -> {
            race.lap();
            race.getCars().forEach(car -> assertThat(car.getCurrentPosition()).isEqualTo(i));
        });
    }

    @DisplayName("test for each lap's position with random engine")
    @RepeatedTest(10)
    void lapWithRandomEngine() {
        final int iterations = 20;
        Race race = new Race(RandomEngine::new, carNames);
        IntStream.range(1, iterations).forEach(i -> {
            race.lap();
            race.getCars().forEach(car -> assertThat(car.getCurrentPosition()).isBetween(0, i));
        });
    }

    @DisplayName("test for perfect engine winner")
    @Test
    void frontLineWithPerfectEngine() {
        final int iterations = 10;
        Race race = new Race(EngineFactory.PERFECT_ENGINE_FACTORY, carNames);
        IntStream.range(0, iterations).forEach(i -> race.lap());
        assertThat(race.getFrontLine()).isEqualTo(race.getCars());
    }

    @DisplayName("test for broken engine winner")
    @Test
    void frontLineWithBrokenEngine() {
        final int iterations = 10;
        Race race = new Race(EngineFactory.BROKEN_ENGINE_FACTORY, carNames);
        IntStream.range(0, iterations).forEach(i -> race.lap());
        assertThat(race.getFrontLine()).isEqualTo(race.getCars());
    }

    @DisplayName("test for modulated engine winner")
    @Test
    void frontLineWithModulatedEngine() {
        final int iterations = 10;
        Race race = new Race(new EngineFactory() {
            boolean firstRunner = true;
            @Override
            public Engine createEngine() {
                if(firstRunner) {
                    firstRunner = false;
                    return Engine.PERFECT_ENGINE;
                }

                return Engine.BROKEN_ENGINE;
            }
        }, carNames);
        IntStream.range(0, iterations).forEach(i -> race.lap());
        assertThat(race.getFrontLine()).isEqualTo(Collections.singletonList(race.getCars().get(0)));
    }
}
