# RealtimeAffectiveData

Language: English | [简体中文](realtime-affective-data-fields.zh-CN.md)

| Property | Type | Description |
| :--: | :--: | :--: |
| realtimeAttentionData | RealtimeAttentionData? | Realtime attention data |
| realtimeRelaxationData | RealtimeRelaxationData? | Realtime relaxation data |
| realtimePressureData | RealtimePressureData? | Realtime pressure level data |
| realtimePleasureData | RealtimePleasureData? | Realtime pleasure data |
| realtimeArousalData | RealtimeArousalData? | Realtime arousal data |
| realtimeSleepData | RealtimeSleepData? | Realtime sleep data |
| realtimeCoherenceData | RealtimeCoherenceData? | Realtime coherence data |
| realtimeFlowData | RealtimeFlowData? | Realtime flow data |

## RealtimeAttentionData

```kotlin
data class RealtimeAttentionData(
    @SerializedName("attention") var attention: Double? = null
){
    override fun toString(): String {
        return "RealtimeAttentionData(attention=$attention)"
    }
}
```

## RealtimeRelaxationData

```kotlin
data class RealtimeRelaxationData(
    @SerializedName("relaxation") var relaxation: Double? = null
){

    override fun toString(): String {
        return "RealtimeRelaxationData(relaxation=$relaxation)"
    }
}
```

## RealtimePressureData

```kotlin
data class RealtimePressureData(
    @SerializedName("pressure") var pressure: Double? = null
){
    override fun toString(): String {
        return "RealtimePressureData(pressure=$pressure)"
    }
}
```

## RealtimePleasureData

```kotlin
data class RealtimePleasureData(
    @SerializedName("pleasure") var pleasure: Double? = null
){
    override fun toString(): String {
        return "RealtimePleasureData(pleasure=$pleasure)"
    }
}
```

## RealtimeArousalData

```kotlin
data class RealtimeArousalData(
    @SerializedName("arousal") var arousal: Double? = null
){
    override fun toString(): String {
        return "RealtimeArousalData(arousal=$arousal)"
    }
}
```

## RealtimeSleepData

```kotlin
data class RealtimeSleepData(
        /**
         * Realtime sleep degree. A smaller value indicates deeper sleep; a larger value indicates a state closer to wakefulness.
         * The realtime sleep degree is not globally corrected, so there is no fixed boundary between awake, light sleep, and deep sleep.
         * Relative changes in sleep degree can still reflect realtime sleep-state trends.
         * Value range: [0, 100]. The value is 0 during initialization and becomes valid after initialization is complete.
         */
        @SerializedName("sleepDegree") var sleepDegree: Double? = null,
        /**
         * Realtime sleep-state decision. It can be used to switch other devices after the user falls asleep.
         * Value range: {0, 1}. The value is 0 before falling asleep and remains 1 after falling asleep.
         */
        @SerializedName("sleepState") var sleepState: Double? = null
) {

    override fun toString(): String {
        return "RealtimeSleepData(sleepDegree=$sleepDegree, sleepState=$sleepState)"
    }
}
```

## RealtimeCoherenceData

```kotlin
data class RealtimeCoherenceData(
    @SerializedName("coherence") var coherence: Double? = null
){
    override fun toString(): String {
        return "RealtimeCoherenceData(coherence=$coherence)"
    }
}
```

## RealtimeFlowData

```kotlin
data class RealtimeFlowData(
    @SerializedName("meditation") var meditation: Double? = null,
    @SerializedName("meditation_tips") var meditationTips: Double? = null
){
    override fun toString(): String {
        return "RealtimeFlowData(meditation=$meditation, meditationTips=$meditationTips)"
    }
}
```
