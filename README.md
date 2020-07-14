# 일상생활 중 건강모니터링을 위한 생체신호 딥러닝 분석 연구
 산학연계SW프로젝트 - 일상생활 중 건강모니터링을 위한 생체신호 딥러닝 분석 연구 (Android) <br />
- Project execution period : 2019.10.01~2020.05.31 <br/>
- Organization (w. link) : 세미콜론 팀 [KW-Semicolon](https://github.com/KW-Semicolon)
- email address : lamlyg@naver.com <br />

<br/>

## Introduction
최근 고령사회로 접어들면서 국내 고혈압 환자는 더욱 증가하고 있는 추세이며, 혈압은 아침에 가장 높고 저녁에 떨어지는 등 정상 상태에서도 변화무쌍하게 달라진다. 미국 연구진의 연구에 따르면, 고혈압으로 치료를 받고 있는 2만 5천 명을 조사한 결과, 동맥경화가 진행되어 혈관이 딱딱할수록 혈압 변화가 크게 나타난다는 것을 알 수 있다. 그렇기 때문에 고혈압 환자는 매일 은 시간에 혈압을 재서, 변화추이를 체크해야할 필요가 있다. 또한 일반적인 혈압 측정 방식은 커프를 팔에 착용한 채로 물리적 압력을 가하여 측정한다. 따라서 장애인들이나 어린이와 같은 이들은 커프를 사용하는 데에 불편이 따를 수 있다. 저희는 커프를 사용하지 않고 더 간편하게 혈압을 측정하기 위해 기술의 발전에 따라 등장한 IoT기술을 접목시키고자 했다.
<br/>

## Project Purpose
혈액이 혈관의 벽에 주는 압력인 혈압은 신체에 물리적인 압력을 가하지 않고 생체신호 하나만으로 정확하게 측정하기가 매우 어렵다. 따라서 혈압을 측정하기 위해서 맥파와 심전도, 최소 두 가지 생체신호가 필요하다. 여기서 새로운 생체신호로 심탄도를 제시하는 바이다. 심탄도란 BCG로 불리며, 심장의 	반동을 측정하여 심장의 상태를 추정하는 계측법을 사용하여 미세한 가속도 변화나 몸의 무게 변화를 측정한 신호이다. 간단하게 말하면 심장에서 얻는 탄성 신호로 비유할 수 있다. 이 BCG생체신호 하나만으로 혈압을 측정해보고자 했다. 추가로 심박, 호흡수까지 추출하여 단 한 번의 측정으로 세 가지 정보를 얻고자 하였다. 또한 커프를 사용하지 않고 간편하게 측정하고자 하였다. 실험자가 편한 상태를 유지할 수 있도록 소파에 BCG 신호를 감지하는 센서를 부착하였다. 실험자가 소파에 앉아있는 동안, 사용자의 생체신호를 추출하고, 이 신호를 분석하여 사용자의 혈압, 심박수, 호흡수, 3가지 정보를 얻어서 어플리케이션을 통해 건강모니터링을 수행하고자 하였다.

## Development implementation contents
사용자가 앉은 의자로부터 BCG 생체 신호를 수집한 뒤 1차적으로 MATLAB을 통해 호흡/심박수를 추출하고 2차적으로 Keras를 통해 딥러닝 과정을 거쳐 혈압이 추정된다. 그리고 마지막으로 안드로이드 앱을 통해 이 결과들(호흡수,심박수,혈압)이 표현되는 것이다. 아래는 그에 따른 처리 흐름도이다.
<br/>

![image](https://user-images.githubusercontent.com/33417495/87445922-9eea2c00-c633-11ea-95a1-f4a8057a9d17.png)

- Deep Learning : https://github.com/KW-Semicolon/Deep-Learning
- Frequency Analysis : https://github.com/KW-Semicolon/Frequency-Analysis

1. 사용자가 의자에 앉으면 엉덩이, 등쪽에 위치한 PVDF 전압 센서를 통해 각각의 위치로부터 BCG 데이터가 수집된다. 이때 1초당 250개의 BCG 데이터가 수집된다. 
2. 수집된 BCG 데이터가 folder1에 파일 형식으로 저장되면 그 파일을 MATLAB에서 불러온다. 
3. 불러온 데이터의 Sampling frequency를 100Hz로 down-sampling한 뒤 MEMD 기법을 사용하여 엉덩이쪽 BCG와 등쪽 BCG 간의 phase difference를 계산하여 folder2에 저장한다. 이는 Keras에서 딥러닝을 하는데 feature로 사용된다. 
4. 설계한 IIR bandpass filter를 거쳐서 indexing된 BCG 데이터를 가지고 분당 호흡수와 심박수를 추출하고 이 결과는 각각 txt 파일형태로 folder3에 저장된다. IIR Filter는 주파수 분석에 사용되는 filter이다.
5. Keras에서는 folder2로부터 가져온 ipd(instantaneous phase difference) data와 사용자의 feature(나이, 키,...)를 사용하여 설계한 딥러닝 모델을 통해 학습시킨다. 
6. 학습의 결과로 추정된 수축시혈압(SBP), 이완시혈압(DBP)이 txt파일 형태로 folder3에 저장된다.
7. folder3에 저장되어 있는 heart.txt(심박수 결과), resp.txt(호흡수 결과), bp.txt(혈압 결과)의 내용을 안드로이드 앱이 읽어서 각각의 결과가 해당하는 건강 단계를 사용자에게 보여준다.

## Development Environment (Android)
### OS 
- Windows 10
### Client
- Android
- Java
- SQLite
<br/>

## Application Version (Android)
- minSdkVersion : 24
- targetSdkVersion : 28

## Library
- [Graph] [Chart](https://github.com/PhilJay/MPAndroidChart) : v2.1.6

<br/>


## UI
<b>메인화면<b/><br/><br/>

![image](https://user-images.githubusercontent.com/33417495/83058181-eb34d900-a092-11ea-9529-1047edac5236.png)
<br/><br/>

<b>메뉴<b/><br/><br/>
 ![image](https://user-images.githubusercontent.com/33417495/83059144-68148280-a094-11ea-90f4-5cae6dbfa008.png)
<br/><br/>
  
<b>HOME - 혈압, 심박, 호흡 결과값 확인 (가로 스와이프형식) / 건강상태 확인<b/> <br/><br/>

![image](https://user-images.githubusercontent.com/33417495/83058284-17505a00-a093-11ea-8eff-b7c02194eb93.png)

<br/>

![image](https://user-images.githubusercontent.com/33417495/83059052-4a471d80-a094-11ea-9a09-d8b9feb24ae9.png)

<br/><br/>

<b>STATUS - 월별 전체기록 확인<b/><br/><br/>

![image](https://user-images.githubusercontent.com/33417495/83058472-626a6d00-a093-11ea-9a5f-937914f8e7d1.png)

<br/><br/>

<b>CHART - 하루 건강 결과값 변화 추이 그래프 확인<b/><br/><br/>
![image](https://user-images.githubusercontent.com/33417495/83058497-69917b00-a093-11ea-9c14-fc40b7b4357c.png)

<br/><br/>

<b>HEALTH - 건강 권고사항 및 TIP / 혈압 가이드 제공<b/><br/><br/>

![image](https://user-images.githubusercontent.com/33417495/83058535-7ada8780-a093-11ea-981e-f139ecbacf77.png)

<br/>

![image](https://user-images.githubusercontent.com/33417495/83059247-94300380-a094-11ea-8600-450c8e589ac3.png)

<br/>

<br/><br/>

<b>MORE - 사용자 정보 확인<b/><br/><br/>

![image](https://user-images.githubusercontent.com/33417495/83058561-8463ef80-a093-11ea-8e56-6b1b8074e41e.png)

<br/><br/>

## Result (Video)
[Demo](https://youtu.be/TotJkS2vBcQ)
<br/>
[Presentation](https://youtu.be/ohXfZt1wx9s)


## License & CopyRight
This project is a source code copyrighted by the Korea Copyright Commission.
