#include "TimerOne.h"
//Формат приема данных "frequence|numberOfPulses",
//где частота - кол-во шагов в секунду, numberOfPulses - количество отсчитываемых импульсов, равных шагам

#define DIR_PIN 2
#define STEP_PIN 3
#define ENABLE_PIN 4 //PULL-UP resistor needed
#define POWER_CONTROL_PIN 5

byte stepPinMask = (1 << STEP_PIN);

// long int absoluteStepCounter = 0; //Определение абсолютного положения в пространстве
long int requiredCount = 0; //Требуемое число шагов
long int stepCounter = 0; //Локальный счетчик шагов для команды
boolean powerOnAction = false;

void setup() {
  pinMode(DIR_PIN, OUTPUT);
  pinMode(STEP_PIN, OUTPUT);
  digitalWrite(STEP_PIN, LOW);
  pinMode(ENABLE_PIN, OUTPUT); //HIGH - off, LOW - on
  digitalWrite(ENABLE_PIN, HIGH);
  pinMode(POWER_CONTROL_PIN, INPUT); //HIGH - PowerOn, LOW - PowerOff
  Serial.begin(115200);
  Serial.setTimeout(30);
  Timer1.initialize();
}

void loop() {

  if (Serial.available())           //Если есть принятый символ,
  {
    String val1 = Serial.readString(); //Сохраняем как строку
    if (val1 == "IsStepper") //Отклик на подключение к ПО
    {
      Serial.write("ImStepper");
      digitalWrite(ENABLE_PIN, LOW); //Подаем напряжение на степпер
      return;
    }
    if (val1 == "StopStepper") //ПО отключилось
    {
      controlledRotation(0);
      digitalWrite(ENABLE_PIN, HIGH); //Снимаем напряжение со степпера
      return;
    }
    if (val1 == "powerOnAction") //Питание только при движении
    {
      powerOnAction = true;
      digitalWrite(ENABLE_PIN, HIGH);
      return;
    }
//     if (val1 == "P") //Периодическая индикация питания
//     {
//       if (digitalRead(POWER_CONTROL_PIN) == HIGH)
//       {
//         Serial.write("N");
//       }
//       else {
//         Serial.write("F");
//       }
//       return;
//     }

    //Если не техданные, то команда
    val1.replace("P", ""); //Если в строке попался запрос, стираем его
    char copy[50];  //В буфер для разделения
    val1.toCharArray(copy, 50);
    String buffer = strtok (copy, "|");
    float val2 = buffer.toFloat(); //Записываем скорость
    buffer = strtok (NULL, "|");
    requiredCount = buffer.toInt(); //Записываем требуемое число импульсов, 0 - если вручную

    controlledRotation(val2);
  }
}

void controlledRotation(float speed) { //Входной аргумент - частота
  if (speed == 0) {
    Timer1.detachInterrupt();
    PORTD &= ~stepPinMask;
    if (powerOnAction) {
    digitalWrite(ENABLE_PIN, HIGH);
    }
    return;
  }
  digitalWrite(ENABLE_PIN, LOW);
  int dir = (speed > 0) ? HIGH : LOW;
  digitalWrite(DIR_PIN, dir);
  speed = abs(speed);

  stepCounter=0;
  Timer1.setPeriod(500000 / speed);
  Timer1.attachInterrupt(timerInterrupt);

}

void timerInterrupt() {
  if (requiredCount != 0 & requiredCount <= stepCounter) {
    Timer1.detachInterrupt(); PORTD &= ~stepPinMask;
    if (powerOnAction) {
    digitalWrite(ENABLE_PIN, HIGH);
    }
    //Destination point reached, send message?
    return;
  }
  if (bitRead(PORTD, STEP_PIN) == 1)
  {
    PORTD &= ~stepPinMask; //Low
    stepCounter++;
//     if (bitRead(PORTD, DIR_PIN) == 1)
//     {
//       absoluteStepCounter++;
//     }
//     else {
//       absoluteStepCounter--;
//     }
  }
  else {
    PORTD |=  stepPinMask; //High
  }
}
