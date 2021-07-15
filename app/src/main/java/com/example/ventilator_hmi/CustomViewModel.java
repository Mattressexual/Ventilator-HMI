package com.example.ventilator_hmi;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;

public class CustomViewModel extends ViewModel {

    private MutableLiveData<Boolean> debug = new MutableLiveData<>(false);

    private MutableLiveData<String> receiveBuffer = new MutableLiveData<>();
    private MutableLiveData<String> bufferRow = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> bufferTokens = new MutableLiveData<>();

    // App state controller values
    private MutableLiveData<Boolean>
            ventRunning = new MutableLiveData<>(),
            saveChanges = new MutableLiveData<>(),
            firstRun = new MutableLiveData<>(),
            usbConnected = new MutableLiveData<>(),
            tempReset = new MutableLiveData<>(),
            attemptUsbConnect = new MutableLiveData<>(),
            resetDefault = new MutableLiveData<>(),
            shutdown = new MutableLiveData<>(),
            readings = new MutableLiveData<>();

    // Ventilation Tab Fragment
    private MutableLiveData<Integer> tabSelected = new MutableLiveData<>(0);
    private MutableLiveData<Integer> ventTabSelectedValue = new MutableLiveData<>();
    private MutableLiveData<Integer> ventTabPrevSelectedValue = new MutableLiveData<>();

    private MutableLiveData<Integer>
            ventMode = new MutableLiveData<>(),
            pip = new MutableLiveData<>(),
            vt = new MutableLiveData<>(),
            pSupport = new MutableLiveData<>(),
            peep = new MutableLiveData<>(),
            rRate = new MutableLiveData<>(),
            backupRate = new MutableLiveData<>(),
            iTimeProgress = new MutableLiveData<>(),
            iPause = new MutableLiveData<>(),
            fio2 = new MutableLiveData<>(),
            pTrigger = new MutableLiveData<>();
    private MutableLiveData<Double> iTime = new MutableLiveData<>();

    private MutableLiveData<Integer>
            tempVentMode = new MutableLiveData<>(),
            tempPip = new MutableLiveData<>(),
            tempVt = new MutableLiveData<>(),
            tempPSupport = new MutableLiveData<>(),
            tempPeep = new MutableLiveData<>(),
            tempRRate = new MutableLiveData<>(),
            tempBackupRate = new MutableLiveData<>(),
            tempITimeProgress = new MutableLiveData<>(),
            tempIPause = new MutableLiveData<>(),
            tempFio2 = new MutableLiveData<>(),
            tempPTrigger = new MutableLiveData<>();

    // Alarm Limits Tab Fragment
    private MutableLiveData<Integer> alarmTabSelectedValue = new MutableLiveData<>();
    private MutableLiveData<Integer> alarmTabPrevSelectedValue = new MutableLiveData<>();

    private MutableLiveData<Integer>
            alarmHiPressure = new MutableLiveData<>(), alarmLoPressure = new MutableLiveData<>(),
            alarmHiPip = new MutableLiveData<>(), alarmLoPip = new MutableLiveData<>(),
            alarmHiPeep = new MutableLiveData<>(), alarmLoPeep = new MutableLiveData<>(),
            alarmHiVt = new MutableLiveData<>(), alarmLoVt = new MutableLiveData<>(),
            alarmHiMVolProgress = new MutableLiveData<>(), alarmLoMVolProgress = new MutableLiveData<>(),
            alarmHiRRate = new MutableLiveData<>(), alarmLoRRate = new MutableLiveData<>();
    private MutableLiveData<Double> alarmHiMVol = new MutableLiveData<>(), alarmLoMVol = new MutableLiveData<>();

    private MutableLiveData<Integer>
            tempHiPressure = new MutableLiveData<>(), tempLoPressure = new MutableLiveData<>(),
            tempHiPip = new MutableLiveData<>(), tempLoPip = new MutableLiveData<>(),
            tempHiPeep = new MutableLiveData<>(), tempLoPeep = new MutableLiveData<>(),
            tempHiVt = new MutableLiveData<>(), tempLoVt = new MutableLiveData<>(),
            tempHiMVolProgress = new MutableLiveData<>(), tempLoMVolProgress = new MutableLiveData<>(),
            tempHiRRate = new MutableLiveData<>(), tempLoRRate = new MutableLiveData<>();
    private MutableLiveData<Double> tempHiMVol = new MutableLiveData<>(), tempLoMVol = new MutableLiveData<>();

    // Waveform Fragment
    private MutableLiveData<Float>
            upperChartValue = new MutableLiveData<>(),
            lowerChartValue = new MutableLiveData<>(),
            inputPress = new MutableLiveData<>(),
            dryAirInFlow = new MutableLiveData<>(),
            oxInFlow = new MutableLiveData<>();

    // Log Fragment
    private MutableLiveData<Alarm> alarm = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Alarm>> activeAlarmList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Alarm>> allAlarmList = new MutableLiveData<>();
    private MutableLiveData<Boolean> refreshAlarmBar = new MutableLiveData<>();
    private MutableLiveData<Boolean> refreshLog = new MutableLiveData<>();

    // Constructor
    public CustomViewModel() {
        // Set default values

        receiveBuffer.setValue("");

        // App state control variables
        ventRunning.setValue(false);
        saveChanges.setValue(false);
        firstRun.setValue(true);
        tempReset.setValue(false);
        usbConnected.setValue(false);
        attemptUsbConnect.setValue(false);
        resetDefault.setValue(false);
        shutdown.setValue(false);

        // Ventilation tab
        ventTabSelectedValue.setValue(0);
        ventTabPrevSelectedValue.setValue(0);

        ventMode.setValue(0);
        pip.setValue(20);
        vt.setValue(500);
        pSupport.setValue(20);
        peep.setValue(0);
        rRate.setValue(5);
        backupRate.setValue(5);
        iTime.setValue(1.2);
        iTimeProgress.setValue((int) (iTime.getValue() * 10f));
        iPause.setValue(0);
        fio2.setValue(50);
        pTrigger.setValue(3);

        tempVentMode.setValue(ventMode.getValue());
        tempPip.setValue(pip.getValue());
        tempVt.setValue(vt.getValue());
        tempPSupport.setValue(pSupport.getValue());
        tempPeep.setValue(peep.getValue());
        tempRRate.setValue(rRate.getValue());
        tempBackupRate.setValue(backupRate.getValue());
        tempITimeProgress.setValue(iTimeProgress.getValue());
        tempIPause.setValue(iPause.getValue());
        tempFio2.setValue(fio2.getValue());
        tempPTrigger.setValue(pTrigger.getValue());

        // Alarm Limits tab values
        alarmTabSelectedValue.setValue(0);
        alarmTabPrevSelectedValue.setValue(0);

        alarmHiPressure.setValue(Constants.DEFAULT_ALARM_PRESSURE_PROGRESS);
        alarmLoPressure.setValue(0);
        alarmHiPip.setValue(Constants.DEFAULT_ALARM_HI_PIP_PROGRESS);
        alarmLoPip.setValue(Constants.DEFAULT_ALARM_LO_PIP_PROGRESS);
        alarmHiPeep.setValue(Constants.DEFAULT_ALARM_HI_PEEP_PROGRESS);
        alarmLoPeep.setValue(Constants.DEFAULT_ALARM_LO_PEEP_PROGRESS);
        alarmHiVt.setValue(Constants.DEFAULT_ALARM_HI_VT_PROGRESS);
        alarmLoVt.setValue(Constants.DEFAULT_ALARM_LO_VT_PROGRESS);
        alarmHiMVol.setValue((double) Constants.DEFAULT_ALARM_HI_MVOL_PROGRESS / 10f + Constants.ALARM_MIN_MVOL);
        alarmLoMVol.setValue((double) Constants.DEFAULT_ALARM_LO_MVOL_PROGRESS / 10f + Constants.ALARM_MIN_MVOL);
        alarmHiMVolProgress.setValue(Constants.DEFAULT_ALARM_HI_MVOL_PROGRESS);
        alarmLoMVolProgress.setValue(Constants.DEFAULT_ALARM_LO_MVOL_PROGRESS);
        alarmHiRRate.setValue(Constants.DEFAULT_ALARM_HI_RRATE_PROGRESS);
        alarmLoRRate.setValue(Constants.DEFAULT_ALARM_LO_RRATE_PROGRESS);

        tempHiPressure.setValue(alarmHiPressure.getValue());
        tempLoPressure.setValue(0);
        tempHiPip.setValue(alarmHiPip.getValue());
        tempLoPip.setValue(alarmLoPip.getValue());
        tempHiPeep.setValue(alarmHiPeep.getValue());
        tempLoPeep.setValue(alarmLoPeep.getValue());
        tempHiVt.setValue(alarmHiVt.getValue());
        tempLoVt.setValue(alarmLoVt.getValue());
        tempHiMVol.setValue(alarmHiMVol.getValue());
        tempLoMVol.setValue(alarmLoMVol.getValue());
        tempHiMVolProgress.setValue(alarmHiMVolProgress.getValue());
        tempLoMVolProgress.setValue(alarmLoMVolProgress.getValue());
        tempHiRRate.setValue(alarmHiRRate.getValue());
        tempLoRRate.setValue(alarmLoRRate.getValue());

        // Waveform Fragment
        upperChartValue.setValue(0f);
        lowerChartValue.setValue(0f);
        inputPress.setValue(0f);
        dryAirInFlow.setValue(0f);
        oxInFlow.setValue(0f);

        // Alarms
        activeAlarmList.setValue(new ArrayList<>());
        allAlarmList.setValue(new ArrayList<>());

        refreshLog.setValue(false);
        refreshAlarmBar.setValue(false);
    }

    // Setters
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setReceiveBuffer(String receiveBuffer) { this.receiveBuffer.setValue(receiveBuffer); }
    public void appendReceiveBuffer(String receiveBuffer) { this.receiveBuffer.setValue(this.receiveBuffer.getValue() + receiveBuffer); }
    public void setBufferRow(String bufferRow) { this.bufferRow.setValue(bufferRow); }
    public void setBufferTokens(ArrayList<String> bufferTokens) { this.bufferTokens.setValue(bufferTokens);}

    // App state controller values
    public void setVentRunning(boolean ventRunning) { this.ventRunning.setValue(ventRunning); }
    public void setSaveChanges(boolean saveChanges) { this.saveChanges.setValue(saveChanges); }
    public void setFirstRun(boolean firstRun) { this.firstRun.setValue(firstRun); }
    public void setResetDefault(boolean resetDefault) { this.resetDefault.setValue(resetDefault); }
    public void setTempReset(boolean tempReset) { this.tempReset.setValue(tempReset); }
    public void setAttemptUsbConnect(boolean attemptUsbConnect) { this.attemptUsbConnect.setValue(attemptUsbConnect); }
    public void setUsbConnected(boolean usbConnected) { this.usbConnected.setValue(usbConnected); }
    public void setShutdown(boolean shutdown) { this.shutdown.setValue(shutdown); }
    public void setReadings(boolean readings) { this.readings.setValue(readings); }

    // Ventilator values
    public void setTabSelected(int tabSelected) { this.tabSelected.setValue(tabSelected); }
    public void setVentTabSelectedValue(int ventTabSelectedValue) { this.ventTabSelectedValue.setValue(ventTabSelectedValue); }
    public void setVentTabPrevSelectedValue(int ventTabPrevSelectedValue) { this.ventTabPrevSelectedValue.setValue(ventTabPrevSelectedValue); }
    public void setVentMode(int ventMode) { this.ventMode.setValue(ventMode); }

    public void setPip(int pip) { this.pip.setValue(pip); }
    public void setVt(int vt) { this.vt.setValue(vt); }
    public void setPSupport(int pSupport) { this.pSupport.setValue(pSupport); }
    public void setPeep(int peep) { this.peep.setValue(peep); }
    public void setRRate(int rRate) { this.rRate.setValue(rRate); }
    public void setBackupRate(int backupRate) { this.backupRate.setValue(backupRate); }
    public void setITime(double iTime) { this.iTime.setValue(iTime); }
    public void setITimeProgress(int iTimeProgress) { this.iTimeProgress.setValue(iTimeProgress); }
    public void setIPause(int iPause) { this.iPause.setValue(iPause); }
    public void setFio2(int fio2) { this.fio2.setValue(fio2); }
    public void setPTrigger(int pTrigger) { this.pTrigger.setValue(pTrigger); }

    public void setTempVentMode(int tempVentMode) { this.tempVentMode.setValue(tempVentMode); }
    public void setTempPip(int tempPip) { this.tempPip.setValue(tempPip); }
    public void setTempVt(int tempVt) { this.tempVt.setValue(tempVt); }
    public void setTempPSupport(int tempPSupport) { this.tempPSupport.setValue(tempPSupport); }
    public void setTempPeep(int tempPeep) { this.tempPeep.setValue(tempPeep); }
    public void setTempRRate(int tempRRate) { this.tempRRate.setValue(tempRRate); }
    public void setTempBackupRate(int tempBackupRate) { this.tempBackupRate.setValue(tempBackupRate); }
    public void setTempITime(int tempITimeProgress) { this.tempITimeProgress.setValue(tempITimeProgress); }
    public void setTempIPause(int tempIPause) { this.tempIPause.setValue(tempIPause); }
    public void setTempFio2(int tempFio2) { this.tempFio2.setValue(tempFio2); }
    public void setTempPTrigger(int tempPTrigger) { this.tempPTrigger.setValue(tempPTrigger); }

    // Alarm Limits values
    public void setAlarmTabSelectedValue(int alarmTabSelectedValue) { this.alarmTabSelectedValue.setValue(alarmTabSelectedValue); }
    public void setAlarmTabPrevSelectedValue(int alarmTabPrevSelectedValue) { this.alarmTabPrevSelectedValue.setValue((alarmTabPrevSelectedValue)); }
    public void setAlarmHiPressure(int alarmHiPressure) { this.alarmHiPressure.setValue(alarmHiPressure); }
    public void setAlarmLoPressure(int alarmLoPressure) { this.alarmLoPressure.setValue(alarmLoPressure); }
    public void setAlarmHiPip(int alarmHiPip) { this.alarmHiPip.setValue(alarmHiPip); }
    public void setAlarmLoPip(int alarmLoPip) { this.alarmLoPip.setValue(alarmLoPip); }
    public void setAlarmHiPeep(int alarmHiPeep) { this.alarmHiPeep.setValue(alarmHiPeep); }
    public void setAlarmLoPeep(int alarmLoPeep) { this.alarmLoPeep.setValue(alarmLoPeep); }
    public void setAlarmHiVt(int alarmHiVt) { this.alarmHiVt.setValue(alarmHiVt); }
    public void setAlarmLoVt(int alarmLoVt) { this.alarmLoVt.setValue(alarmLoVt); }
    public void setAlarmHiMVol(double alarmHiMVol) { this.alarmHiMVol.setValue(alarmHiMVol); }
    public void setAlarmLoMVol(double alarmLoMVol) { this.alarmLoMVol.setValue(alarmLoMVol); }
    public void setAlarmHiMVolProgress(int alarmHiMVolProgress) { this.alarmHiMVolProgress.setValue(alarmHiMVolProgress); }
    public void setAlarmLoMVolProgress(int alarmLoMVolProgress) { this.alarmLoMVolProgress.setValue(alarmLoMVolProgress); }
    public void setAlarmHiRRate(int alarmHiRRate) { this.alarmHiRRate.setValue(alarmHiRRate); }
    public void setAlarmLoRRate(int alarmLoRRate) { this.alarmLoRRate.setValue(alarmLoRRate); }
    public void setTempHiPressure(int tempHiPressure) { this.tempHiPressure.setValue(tempHiPressure); }
    public void setTempLoPressure(int tempLoPressure) { this.tempLoPressure.setValue(tempLoPressure); }
    public void setTempHiPip(int tempHiPip) { this.tempHiPip.setValue(tempHiPip); }
    public void setTempLoPip(int tempLoPip) { this.tempLoPip.setValue(tempLoPip); }
    public void setTempHiPeep(int tempHiPeep) { this.tempHiPeep.setValue(tempHiPeep); }
    public void setTempLoPeep(int tempLoPeep) { this.tempLoPeep.setValue(tempLoPeep); }
    public void setTempHiVt(int tempHiVt) { this.tempHiVt.setValue(tempHiVt); }
    public void setTempLoVt(int tempLoVt) { this.tempLoVt.setValue(tempLoVt); }
    public void setTempHiMVol(double tempHiMVol) { this.tempHiMVol.setValue(tempHiMVol); }
    public void setTempLoMVol(double tempLoMVol) { this.tempLoMVol.setValue(tempLoMVol); }
    public void setTempHiMVolProgress(int tempHiMVolProgress) { this.tempHiMVolProgress.setValue(tempHiMVolProgress); }
    public void setTempLoMVolProgress(int tempLoMVolProgress) { this.tempLoMVolProgress.setValue(tempLoMVolProgress); }
    public void setTempHiRRate(int tempHiRRate) { this.tempHiRRate.setValue(tempHiRRate); }
    public void setTempLoRRate(int tempLoRRate) { this.tempLoRRate.setValue(tempLoRRate); }

    // Waveform Fragment
    public void setUpperChartValue(float upperChartValue) { this.upperChartValue.setValue(upperChartValue); }
    public void setLowerChartValue(float lowerChartValue) { this.lowerChartValue.setValue(lowerChartValue); }
    public void setInputPress(float inputPress) { this.inputPress.setValue(inputPress); }
    public void setDryAirInFlow(float dryAirInFlow) { this.dryAirInFlow.setValue(dryAirInFlow); }
    public void setOxInFlow(float oxInFlow) { this.oxInFlow.setValue(oxInFlow); }

    // Log Fragment
    public void setAlarm(Alarm alarm) {
        this.alarm.setValue(alarm);
    }

    public void addActiveAlarm(Alarm alarm) {
        activeAlarmList.getValue().add(alarm);
        allAlarmList.getValue().add(alarm);
    }
    public void clearActiveAlarm(Alarm alarm) {
        activeAlarmList.getValue().remove(alarm);
        allAlarmList.getValue().get(allAlarmList.getValue().indexOf(alarm)).setCleared(true);
        allAlarmList.getValue().get(allAlarmList.getValue().indexOf(alarm)).setEndTime(new Date());
    }

    public void removeAlarm(Alarm alarm) {
        allAlarmList.getValue().remove(alarm);
    }

    public void setRefreshAlarmBar(boolean refreshAlarmBar) { this.refreshAlarmBar.setValue(refreshAlarmBar); }
    public void setRefreshLog(boolean refreshLog) { this.refreshLog.setValue(refreshLog); }


    // Getters
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public MutableLiveData<Boolean> getDebug() { return this.debug; }

    public MutableLiveData<String> getReceiveBuffer() { return this.receiveBuffer; }
    public MutableLiveData<String> getBufferRow() { return this.bufferRow; }
    public MutableLiveData<ArrayList<String>> getBufferTokens() { return this.bufferTokens; }

    // App state controller values
    public MutableLiveData<Boolean> getVentRunning() { return this.ventRunning; }
    public MutableLiveData<Boolean> getSaveChanges() { return this.saveChanges; }
    public MutableLiveData<Boolean> getFirstRun() { return this.firstRun; }
    public MutableLiveData<Boolean> getTempReset() { return this.tempReset; }
    public MutableLiveData<Boolean> getResetDefault() { return this.resetDefault; }
    public MutableLiveData<Boolean> getAttemptUsbConnect() { return this.attemptUsbConnect; }
    public MutableLiveData<Boolean> getUsbConnected() { return this.usbConnected; }
    public MutableLiveData<Boolean> getShutdown() { return this.shutdown; }
    public MutableLiveData<Boolean> getReadings() { return this.readings; }

    // Ventilator values
    public MutableLiveData<Integer> getTabSelected() { return this.tabSelected; }
    public MutableLiveData<Integer> getVentTabSelectedValue() { return this.ventTabSelectedValue; }
    public MutableLiveData<Integer> getVentTabPrevSelectedValue() { return this.ventTabPrevSelectedValue; }
    public MutableLiveData<Integer> getVentMode() { return this.ventMode; }
    public MutableLiveData<Integer> getPip() { return this.pip; }
    public MutableLiveData<Integer> getVt() { return this.vt; }
    public MutableLiveData<Integer> getPSupport() { return this.pSupport; }
    public MutableLiveData<Integer> getPeep() { return this.peep; }
    public MutableLiveData<Integer> getRRate() { return this.rRate; }
    public MutableLiveData<Integer> getBackupRate() { return this.backupRate; }
    public MutableLiveData<Double> getITime() { return this.iTime; }
    public MutableLiveData<Integer> getITimeProgress() { return this.iTimeProgress; }
    public MutableLiveData<Integer> getIPause() { return this.iPause; }
    public MutableLiveData<Integer> getFio2() { return this.fio2; }
    public MutableLiveData<Integer> getPTrigger() { return this.pTrigger; }
    public MutableLiveData<Integer> getTempVentMode() { return this.tempVentMode; }
    public MutableLiveData<Integer> getTempPip() { return this.tempPip; }
    public MutableLiveData<Integer> getTempVt() { return this.tempVt; }
    public MutableLiveData<Integer> getTempPSupport() { return this.tempPSupport; }
    public MutableLiveData<Integer> getTempPeep() { return this.tempPeep; }
    public MutableLiveData<Integer> getTempRRate() { return this.tempRRate; }
    public MutableLiveData<Integer> getTempBackupRate() { return this.tempBackupRate; }
    public MutableLiveData<Integer> getTempITimeProgress() { return this.tempITimeProgress; }
    public MutableLiveData<Integer> getTempIPause() { return this.tempIPause; }
    public MutableLiveData<Integer> getTempFio2() { return this.tempFio2; }
    public MutableLiveData<Integer> getTempPTrigger() { return this.tempPTrigger; }

    // Alarm Limits values
    public MutableLiveData<Integer> getAlarmTabSelectedValue() { return alarmTabSelectedValue; }
    public MutableLiveData<Integer> getAlarmTabPrevSelectedValue() { return alarmTabPrevSelectedValue; }
    public MutableLiveData<Integer> getAlarmHiPressure() { return alarmHiPressure; }
    public MutableLiveData<Integer> getAlarmLoPressure() { return alarmLoPressure; }
    public MutableLiveData<Integer> getAlarmHiPip() { return alarmHiPip; }
    public MutableLiveData<Integer> getAlarmLoPip() { return alarmLoPip; }
    public MutableLiveData<Integer> getAlarmHiPeep() { return alarmHiPeep; }
    public MutableLiveData<Integer> getAlarmLoPeep() { return alarmLoPeep; }
    public MutableLiveData<Integer> getAlarmHiVt() { return alarmHiVt; }
    public MutableLiveData<Integer> getAlarmLoVt() { return alarmLoVt; }
    public MutableLiveData<Double> getAlarmHiMVol() { return alarmHiMVol; }
    public MutableLiveData<Double> getAlarmLoMVol() { return alarmLoMVol; }
    public MutableLiveData<Integer> getAlarmHiMVolProgress() { return alarmHiMVolProgress; }
    public MutableLiveData<Integer> getAlarmLoMVolProgress() { return alarmLoMVolProgress; }
    public MutableLiveData<Integer> getAlarmHiRRate() { return alarmHiRRate; }
    public MutableLiveData<Integer> getAlarmLoRRate() { return alarmLoRRate; }
    public MutableLiveData<Integer> getTempHiPressure() { return tempHiPressure; }
    public MutableLiveData<Integer> getTempLoPressure() { return tempLoPressure; }
    public MutableLiveData<Integer> getTempHiPip() { return tempHiPip; }
    public MutableLiveData<Integer> getTempLoPip() { return tempLoPip; }
    public MutableLiveData<Integer> getTempHiPeep() { return tempHiPeep; }
    public MutableLiveData<Integer> getTempLoPeep() { return tempLoPeep; }
    public MutableLiveData<Integer> getTempHiVt() { return tempHiVt; }
    public MutableLiveData<Integer> getTempLoVt() { return tempLoVt; }
    public MutableLiveData<Double> getTempHiMVol() { return tempHiMVol; }
    public MutableLiveData<Double> getTempLoMVol() { return tempLoMVol; }
    public MutableLiveData<Integer> getTempHiMVolProgress() { return tempHiMVolProgress; }
    public MutableLiveData<Integer> getTempLoMVolProgress() { return tempLoMVolProgress; }
    public MutableLiveData<Integer> getTempHiRRate() { return tempHiRRate; }
    public MutableLiveData<Integer> getTempLoRRate() { return tempLoRRate; }

    public boolean getAlarmRangesOkay() {
        return tempHiPip.getValue() > tempLoPip.getValue()
                && tempHiPeep.getValue() > tempLoPeep.getValue()
                && tempHiVt.getValue() > tempLoVt.getValue()
                && tempHiMVolProgress.getValue() > tempLoMVolProgress.getValue()
                && tempHiRRate.getValue() > tempLoRRate.getValue();
    }

    // Waveform Fragment
    public MutableLiveData<Float> getUpperChartValue() { return upperChartValue; }
    public MutableLiveData<Float> getLowerChartValue() { return lowerChartValue; }
    public MutableLiveData<Float> getInputPress() { return inputPress; }
    public MutableLiveData<Float> getDryAirInFlow() { return dryAirInFlow; }
    public MutableLiveData<Float> getOxInFlow() { return oxInFlow; }

    // Log Fragment
    public MutableLiveData<Alarm> getAlarm() { return alarm; }
    public MutableLiveData<ArrayList<Alarm>> getActiveAlarmList() { return activeAlarmList; }
    public MutableLiveData<ArrayList<Alarm>> getAllAlarmList() { return allAlarmList; }
    public MutableLiveData<Boolean> getRefreshAlarmBar() { return refreshAlarmBar; }
    public MutableLiveData<Boolean> getRefreshLog() { return refreshLog; }
}
