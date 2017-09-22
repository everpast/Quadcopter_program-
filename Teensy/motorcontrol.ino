boolean initial;
char test,temp, data[100];
//String data; 
String message[4];
int mark,count;
int num;
int t[4];
int i,j;
void setup() {
  // put your setup code here, to run once:
Serial.begin(38400);

pinMode(2, OUTPUT);
pinMode(3, OUTPUT);
pinMode(4, OUTPUT);
pinMode(5, OUTPUT);
analogWriteFrequency(2, 50);
analogWriteFrequency(3, 50);
analogWriteFrequency(4, 50);
analogWriteFrequency(5, 50);
analogWriteResolution(14);  // analogWrite value 0 to 16383, or 16383 for high
initial=false;//from 1000 to 2000
}

void loop() {
  if (initial ==false){
    initial= true;
    t[0]=1000;
    t[1]=1000;
    t[2]=1000;
    t[3]=1000;
    }
  // put your main code here, to run repeatedly:
while(Serial.available()>0) {
  
  data[count]=char(Serial.read());
  mark=1;
  count++;
}

if(mark==1){
Serial.print("system received: ");
Serial.println(data);
//num=data.toInt();
//Serial.print("number:");
//Serial.println(num);

for(i=0;i<=19;i++){
    
 
  
    temp=data[i];
    if (temp!=','){
      message[j]+=temp;}
      else{
        t[j]=message[j].toInt();
        message[j]="";
        j++;}
    }
t[j]=message[j].toInt();
message[j]="";
mark=0;
count=0;
j=0;
memset(data, 0x0, strlen(data));
Serial.println( t[0] );
Serial.println( t[1] );
Serial.println( t[2] );
Serial.println( t[3] );
}
for (i=0;i<4;i++){
  if(t[i]<1000) t[i]=1000;
  if(t[i]>2000) t[i]=2000;
  }
analogWrite(2, t[0]);
analogWrite(3, t[1]);
analogWrite(4, t[2]);
analogWrite(5, t[3]);  
}
