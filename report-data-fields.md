# UploadReportEntity

Language: English | [简体中文](report-data-fields.zh-CN.md)

| Property | Type | Description |
| :--: | :--: | :--: |
| data | Data? | Bio basic data and affective basic data |

## Data

| Parameter | Type | Description |
| :--: | :--: | :--: |
| affective | Affective | Affective basic data |
| biodata | Biodata | Bio basic data |

### Affective

| Property | Type | Description |
| :--: | :--: | :--: |
| arousal | Arousal | Arousal report |
| attention | Attention | Attention report |
| coherence | Coherence | Coherence report |
| pleasure | Pleasure | Pleasure report |
| pressure | Pressure | Pressure report |
| relaxation | Relaxation | Relaxation report |
| meditation | Meditation | Meditation report |
| sleep | Sleep | Sleep report |

#### Arousal

```kotlin
data class Arousal(
        /**
         * Average valid arousal value for the full session, excluding invalid 0 values.
         */
        val arousal_avg: Int,
        /**
         * Full-session arousal record.
         */
        val arousal_rec: Any
)
```

#### Attention

```kotlin
data class Attention(
    /**
     * Average valid attention value for the full session, excluding invalid 0 values.
     */
    @SerializedName("attention_avg")
    val attentionAvg: Double,
    /**
     * Full-session attention record.
     */
    @SerializedName("attention_rec")
    val attentionRec: List<Double>
)
```

#### Coherence

```kotlin
data class Coherence(
    /**
     * Average valid coherence value for the full session, excluding invalid 0 values.
     */
    @SerializedName("coherence_avg")
    val coherenceAvg: Double,
    @SerializedName("coherence_duration")
    val coherenceDuration: Int?,
    @SerializedName("coherence_flag")
    val coherenceFlag: List<Int>?,
    /**
     * Full-session coherence record.
     */
    @SerializedName("coherence_rec")
    val coherenceRec: List<Double>
)
```

#### Pleasure

```kotlin
data class Pleasure(
        /**
         * Average valid pleasure value for the full session, excluding invalid 0 values.
         */

        val pleasureAvg: Double,
        /**
         * Full-session pleasure record.
         */

        val pleasureRec: List<Double>
)
```

#### Pressure

```kotlin
data class Pressure(

        val pressureAvg: Double,

        val pressureRec: List<Double>
)
```

#### Relaxation

```kotlin
data class Relaxation(
        /**
         * Average valid relaxation value for the full session, excluding invalid 0 values.
         */

        val relaxationAvg: Double,
        /**
         * Full-session relaxation record.
         */

        val relaxationRec: List<Double>
)
```

#### Meditation

```kotlin
data class Meditation(

        val meditationAvg: Double,

        val meditationRec: List<Double>,

        val meditationTipsRec: List<Int>,

        val flowPercent: Double,

        val flowDuration: Int,

        val flowLatency: Int,

        val flowCombo: Int,

        val flowDepth: Double,

        val flowBackNum: Int,

        val flowLossNum: Int,
        )
```

#### Sleep

```kotlin
data class Sleep(
    /**
     * Sleep curve for the full experience.
     * A higher curve value indicates a state closer to wakefulness; a lower curve value indicates a state closer to deep sleep.
     */
    val sleepCurve: ArrayList<Double> = ArrayList(),
    /**
     * Sleep-onset time index, which is the time-axis coordinate of the sleep onset point on the sleep curve.
     * Value range: [0, +infinity). 0 indicates an invalid value.
     */
    val sleepPoint: Int = 0,
    /**
     * Sleep latency. Unit: seconds.
     */
    val sleepLatency: Int = 0,
    /**
     * Awake duration. Unit: seconds.
     */
    val awakeDuration: Int = 0,
    /**
     * Light sleep duration. Unit: seconds.
     */
    val lightDuration: Int = 0,
    /**
     * Deep sleep duration. Unit: seconds.
     */
    val deepDuration: Int = 0,
    /**
     * REM duration.
     */
    var remDuration: Int = 0,
    /**
     * Movement count.
     */
    var movementCount: Int = 0,
    /**
     * Arousal count.
     */
    var arousalCount: Int = 0,
    /**
     * Disturbance tolerance.
     */
    var disturbTolerance: Double = 0.0,

    val sleepEegAlphaCurve: List<Double> = ArrayList(),

    val sleepEegBetaCurve: List<Double> =
        ArrayList(),

    val sleepEegThetaCurve: List<Double> =
        ArrayList(),

    val sleepEegDeltaCurve: List<Double> =
        ArrayList(),

    val sleepEegGammaCurve: List<Double> =
        ArrayList(),

    val sleepEegQualityRec: List<Int> =
        ArrayList(),

    val sleepMovementRec: List<Int> =
        ArrayList(),

    val sleepArousalRec: List<Int> = ArrayList()
)
```

### Biodata

| Property | Type | Description |
| :--: | :--: | :--: |
| data | Sceeg | Single-channel EEG data |
| eeg | Eeg | EEG data |
| hr | HrV2 | Heart rate and HRV data |
| pepr | PEPR? | PEPR data |

#### Sceeg

```kotlin
data class Sceeg(
    val sceegAlphaCurve: List<Double>,
    val scegBetaCurve: List<Double>,
    val sceegDeltaCurve: List<Double>,
    val sceegGammaCurve: List<Double>,
    val sceegThetaCurve: List<Double>,
    val sceegQualityRec: List<Int>
)
```

#### Eeg

```kotlin
data class Eeg(

        val eegAlphaCurve: List<Double>,

        val eegBetaCurve: List<Double>,

        val eegDeltaCurve: List<Double>,

        val eegGammaCurve: List<Double>,

        val eegThetaCurve: List<Double>,

        val eegQualityRec: List<Int>
)
```

#### HrV2

```kotlin
data class HrV2(

        val hrAvg: Double?,

        val hrMax: Int?,

        val hrMin: Int?,

        val hrRec: List<Int>,

        val hrvAvg: Double?,

        val hrvRec: List<Double>
)
```

#### PEPR

| Property | Type | Description |
| :--: | :--: | :--: |
| hrAvg | Int | Average heart rate |
| hrMax | Int | Maximum heart rate |
| hrMin | Int | Minimum heart rate |
| hrRec | List\<Int> | Heart rate record |
| hrvAvg | Double | Average heart rate variability |
| hrvRec | List\<Double> | Heart rate variability record |
| rrAvg | Double | Average respiration rate |
| rrRec | List\<Double> | Respiration rate record |
| bcgQualityRec | List\<Int> | Pulse wave quality record |
| rwQualityRec | List\<Int> | Respiration wave quality record |
