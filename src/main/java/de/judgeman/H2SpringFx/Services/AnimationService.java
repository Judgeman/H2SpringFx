package de.judgeman.H2SpringFx.Services;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import org.springframework.stereotype.Service;

/**
 * Created by Paul Richter on Tue 08/09/2020
 */
@Service
public class AnimationService {

    public final double DEFAULT_DURATION_FADE_IN = 200;
    public final double DEFAULT_DURATION_FADE_OUT = DEFAULT_DURATION_FADE_IN;

    public final double DEFAULT_DURATION_BOUNCE_SCALE = 150;
    public final double DEFAULT_DURATION_BOUNCE_IN = 50;
    public final double DEFAULT_DURATION_BOUNCE_OUT = 300;

    public final double SCALE_BOUNCE_TRANSITION_FROM_X = 0.0;
    public final double SCALE_BOUNCE_TRANSITION_FROM_Y = 0.0;
    public final double SCALE_BOUNCE_TRANSITION_TO_X = 1.0;
    public final double SCALE_BOUNCE_TRANSITION_TO_Y = 1.0;
    public final double SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_BIG_X = 1.05;
    public final double SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_BIG_Y = 1.05;
    public final double SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_SMALL_X = 0.95;
    public final double SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_SMALL_Y = 0.95;

    public final double FADE_IN_TRANSITION_FROM = 0.0;
    public final double FADE_IN_TRANSITION_TO = 1.0;

    public final double FADE_OUT_TRANSITION_FROM = FADE_IN_TRANSITION_TO;
    public final double FADE_OUT_TRANSITION_TO = FADE_IN_TRANSITION_FROM;

    public SequentialTransition createBounceInTransition(Node pane) {
        return createBounceInTransition(pane, Duration.millis(DEFAULT_DURATION_BOUNCE_SCALE), Duration.millis(DEFAULT_DURATION_BOUNCE_IN));
    }

    public SequentialTransition createBounceInTransition(Node pane, Duration scaleDuration, Duration bounceDuration) {
        ScaleTransition scaleFromSmallToBigTransition = new ScaleTransition(scaleDuration, pane);
        scaleFromSmallToBigTransition.setFromX(SCALE_BOUNCE_TRANSITION_FROM_X);
        scaleFromSmallToBigTransition.setFromY(SCALE_BOUNCE_TRANSITION_FROM_Y);
        scaleFromSmallToBigTransition.setToX(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_BIG_X);
        scaleFromSmallToBigTransition.setToY(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_BIG_Y);

        ScaleTransition scaleFromBigToSmallerTransition = new ScaleTransition(bounceDuration, pane);
        scaleFromBigToSmallerTransition.setFromX(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_BIG_X);
        scaleFromBigToSmallerTransition.setFromY(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_BIG_Y);
        scaleFromBigToSmallerTransition.setToX(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_SMALL_X);
        scaleFromBigToSmallerTransition.setToY(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_SMALL_Y);

        ScaleTransition scaleFromSmallerToRightSizeTransition = new ScaleTransition(bounceDuration, pane);
        scaleFromSmallerToRightSizeTransition.setFromX(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_SMALL_X);
        scaleFromSmallerToRightSizeTransition.setFromY(SCALE_BOUNCE_TRANSITION_BOUNCE_POSITON_SMALL_Y);
        scaleFromSmallerToRightSizeTransition.setToX(SCALE_BOUNCE_TRANSITION_TO_X);
        scaleFromSmallerToRightSizeTransition.setToY(SCALE_BOUNCE_TRANSITION_TO_Y);

        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(scaleFromSmallToBigTransition, scaleFromBigToSmallerTransition, scaleFromSmallerToRightSizeTransition);
        return sequentialTransition;
    }

    public ScaleTransition createBounceOutTransition(Node element) {
        return createBounceOutTransition(element, Duration.millis(DEFAULT_DURATION_BOUNCE_OUT));
    }

    public ScaleTransition createBounceOutTransition(Node element, Duration duration) {
        ScaleTransition bounceTransition = new ScaleTransition(duration, element);
        bounceTransition.setFromX(SCALE_BOUNCE_TRANSITION_TO_X);
        bounceTransition.setFromY(SCALE_BOUNCE_TRANSITION_TO_Y);
        bounceTransition.setToX(SCALE_BOUNCE_TRANSITION_FROM_X);
        bounceTransition.setToY(SCALE_BOUNCE_TRANSITION_FROM_Y);

        return bounceTransition;
    }

    public FadeTransition createFadeInTransition(Node element) {
        return createFadeInTransition(element, Duration.millis(DEFAULT_DURATION_FADE_IN));
    }

    public FadeTransition createFadeInTransition(Node element, Duration duration) {
        FadeTransition fadeInTransition = new FadeTransition(duration, element);
        fadeInTransition.setFromValue(FADE_IN_TRANSITION_FROM);
        fadeInTransition.setToValue(FADE_IN_TRANSITION_TO);

        return fadeInTransition;
    }

    public FadeTransition createFadeOutTransition(Node element) {
        return createFadeOutTransition(element, Duration.millis(DEFAULT_DURATION_FADE_OUT));
    }

    public FadeTransition createFadeOutTransition(Node element, Duration duration) {
        FadeTransition fadeOutTransition = new FadeTransition(duration, element);
        fadeOutTransition.setFromValue(FADE_OUT_TRANSITION_FROM);
        fadeOutTransition.setToValue(FADE_OUT_TRANSITION_TO);

        return fadeOutTransition;
    }
}
