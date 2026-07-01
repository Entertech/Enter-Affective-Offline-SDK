# RealtimeBioData

Language: English | [简体中文](realtime-bio-data-fields.zh-CN.md)

| Property | Type | Description |
| :--: | :--: | :--: |
| realtimeEEGData | RealtimeEEGData? | Realtime EEG data |
| realtimeHrData | RealtimeHrData? | Realtime heart rate data |
| realtimeSCEEGData | RealtimeSCEEGData? | Realtime single-channel EEG data |
| realtimePEPRData | RealtimePEPRData? | Realtime PEPR data |

## RealtimeEEGData

```kotlin
class RealtimeEEGData {
    /**
     * Filtered realtime EEG waveform from the left channel.
     * The left and right channels each contain an array of length 150, corresponding to the EEG waveform within 0.6 seconds.
     * Value range: [-500, 500]. Values are all 0 when signal quality is poor.
     */
    var leftwave: ArrayList<Double>? = null

    /**
     * Filtered realtime EEG waveform from the right channel.
     * The left and right channels each contain an array of length 150, corresponding to the EEG waveform within 0.6 seconds.
     * Value range: [-500, 500]. Values are all 0 when signal quality is poor.
     */
    var rightwave: ArrayList<Double>? = null

    // Power values in decibels for five EEG rhythms: alpha, beta, theta, delta, and gamma.
    // Each rhythm returns one value. Value range: [0, +infinity). Values are 0 during initialization or when signal quality is poor.

    var alphaPower: Double? = null
    var betaPower: Double? = null
    var thetaPower: Double? = null
    var deltaPower: Double? = null
    var gammaPower: Double? = null

    /**
     * EEG signal quality level. A value >= 1 indicates good EEG signal quality.
     */
    var quality: Double? = null

    override fun toString(): String {
        return "RealtimeEEGDataEntity(leftwave=$leftwave, rightwave=$rightwave, alphaPower=$alphaPower, betaPower=$betaPower, thetaPower=$thetaPower, deltaPower=$deltaPower, gammaPower=$gammaPower, quality=$quality)"
    }

}
```

## RealtimeHrData

```kotlin
data class RealtimeHrData(
        /**
         * Realtime heart rate value. Value range: [0, 255]. Unit: BPM.
         */
        @SerializedName("hr") var hr: Double? = null,
        /**
         * Realtime heart rate variability. Value range: [0, +infinity).
         */
        @SerializedName("hrv") var hrv: Double? = null
) {
    override fun toString(): String {
        return "RealtimeHrData(hr=$hr, hrv=$hrv)"
    }
}
```

## RealtimeSCEEGData

```kotlin
data class RealtimeSCEEGData(
        /**
         * Filtered realtime EEG waveform from one channel.
         * The array length is 150, corresponding to the EEG waveform within 0.6 seconds.
         * Value range: [-500, 500]. Values are all 0 when signal quality is poor.
         */
        val sceegWave: List<Double> = emptyList(),
        // Power values in decibels for five EEG rhythms: alpha, beta, theta, delta, and gamma.
        // Each rhythm returns one value. Value range: [0, +infinity). Values are 0 during initialization or when signal quality is poor.
        val sceegAlphaPower: Double = 0.0,
        val sceegBetaPower: Double = 0.0,
        val sceegThetaPower: Double = 0.0,
        val sceegDeltaPower: Double = 0.0,
        val sceegGammaPower: Double = 0.0,
        /**
         * EEG signal quality level.
         * A value greater than 1 indicates good EEG signal quality.
         */
        val sceegQuality: Double = 0.0,
        )
```

## RealtimePEPRData

```kotlin
class RealtimePEPRData {
    // Pulse wave.
    var bcgWave: ArrayList<Double>? = null
    // Respiration wave.
    var rwWave: ArrayList<Double>? = null
    // Pulse wave quality level. 0: not worn; 1: data exists but no signal; 2: data exists and signal is good.
    var bcgQuality: Int? = null
    // Respiration wave quality level. 0: not worn; 1: data exists but no signal; 2: data exists and signal is good.
    var rwQuality: Int? = null
    // Heart rate value. Unit: BPM.
    var hr: Double? = null
    // Heart rate variability value. Unit: milliseconds.
    var hrv: Double? = null
    // Respiration rate.
    var rr:Double? = null
}
```
