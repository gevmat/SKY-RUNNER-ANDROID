package com.skyrunner.game;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.graphics.*;
import java.util.*;
public class GameActivity extends Activity {
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);setContentView(new GameView());}
 class GameView extends View {
 final Paint p=new Paint(3); final float W=960,H=540,G=455,PW=34,PH=44;
 float x=70,y=G-PH,vx,vy,camera;boolean left,right,jump,grounded,won,dead;int level=0,lives=3,score=0;long last=System.nanoTime();
 final float[] lengths={2450,3000,3550};ArrayList<RectF> platforms=new ArrayList<>(),hazards=new ArrayList<>();ArrayList<float[]> coins=new ArrayList<>();
 GameView(){super(GameActivity.this);load();}
 void load(){platforms.clear();hazards.clear();coins.clear();x=70;y=G-PH;vx=vy=camera=0;won=dead=false;
 float end=lengths[level];for(float a=0;a<end;a+=520)platforms.add(new RectF(a,G,a+430,540));
 for(int i=0;i<7+level*2;i++){float a=320+i*290,h=55+(i%3)*38;platforms.add(new RectF(a,G-h,a+115,G-h+17));coins.add(new float[]{a+55,G-h-35,0});if(i%2==0)coins.add(new float[]{a+125,G-38,0});}
 for(int i=0;i<5+level;i++){float a=620+i*390;hazards.add(new RectF(a,G-27,a+36,G));}}
 void color(int c){p.setColor(c);p.setStyle(Paint.Style.FILL);}
 void rect(Canvas c,float a,float b,float d,float e,int col){color(col);c.drawRect(a,b,d,e,p);}
 void text(Canvas c,String s,float xx,float yy,int size,int col){color(col);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(size);c.drawText(s,xx,yy,p);}
 boolean intersects(RectF a,RectF b){return a.left<b.right&&a.right>b.left&&a.top<b.bottom&&a.bottom>b.top;}
 void hit(){lives--;if(lives<=0)dead=true;else{x=(float)Math.floor(Math.max(0,x-120)/520)*520+45;y=G-PH;vy=0;}}
 void step(float dt){if(won||dead)return;dt=Math.min(dt,.033f);vx=(right?230:0)-(left?230:0);
 if(jump&&grounded){vy=-490;grounded=false;}jump=false;float oldBottom=y+PH;x+=vx*dt;x=Math.max(0,Math.min(lengths[level]-PW,x));vy+=1150*dt;y+=vy*dt;grounded=false;
 RectF player=new RectF(x,y,x+PW,y+PH);for(RectF platform:platforms)if(intersects(player,platform)&&vy>=0&&oldBottom<=platform.top+13){y=platform.top-PH;vy=0;grounded=true;player.set(x,y,x+PW,y+PH);}
 for(RectF h:hazards)if(intersects(player,h)){hit();return;}if(y>H+100){hit();return;}
 for(float[] coin:coins)if(coin[2]==0&&Math.abs(x+PW/2-coin[0])<27&&Math.abs(y+PH/2-coin[1])<31){coin[2]=1;score+=10;}
 if(x>lengths[level]-105){if(level<2){level++;load();}else won=true;}camera=Math.max(0,Math.min(lengths[level]-W,x-250));}
 @Override protected void onDraw(Canvas real){super.onDraw(real);float scale=Math.min(getWidth()/W,getHeight()/H),ox=(getWidth()-W*scale)/2,oy=(getHeight()-H*scale)/2;
 real.drawColor(Color.rgb(15,24,49));real.save();real.translate(ox,oy);real.scale(scale,scale);long now=System.nanoTime();step((now-last)/1e9f);last=now;
 rect(real,0,0,W,H,0xff6ccbf7);color(0x55ffffff);for(int i=0;i<12;i++){float cx=(i*290-camera*.23f)%1250;real.drawOval(cx,75+(i%3)*38,cx+120,115+(i%3)*38,p);}
 real.save();real.translate(-camera,0);for(RectF plat:platforms){rect(real,plat.left,plat.top,plat.right,plat.bottom,0xff7b4a2d);rect(real,plat.left,plat.top,plat.right,plat.top+11,0xff58b74e);}
 for(RectF h:hazards){Path path=new Path();path.moveTo(h.left,h.bottom);path.lineTo((h.left+h.right)/2,h.top);path.lineTo(h.right,h.bottom);path.close();color(0xffe63c53);real.drawPath(path,p);}
 for(float[] coin:coins)if(coin[2]==0){color(0xffffd23f);real.drawCircle(coin[0],coin[1],13,p);color(0xffffef99);real.drawCircle(coin[0]-3,coin[1]-3,5,p);}
 float flag=lengths[level]-85;rect(real,flag,G-160,flag+7,G,0xfff2f2f2);color(0xffff5d72);Path triangle=new Path();triangle.moveTo(flag+7,G-158);triangle.lineTo(flag+75,G-134);triangle.lineTo(flag+7,G-110);triangle.close();real.drawPath(triangle,p);
 rect(real,x+4,y+10,x+PW-4,y+PH,0xff275cc7);rect(real,x,y+5,x+PW,y+21,0xfff5a85d);rect(real,x-2,y,x+PW+2,y+10,0xffdb364c);rect(real,x+22,y+11,x+27,y+16,Color.BLACK);real.restore();
 rect(real,0,0,W,56,0x880c1b36);text(real,"SKY RUNNER",20,38,30,Color.WHITE);text(real,"LEVEL "+(level+1)+" / 3",330,36,24,Color.WHITE);text(real,"COINS "+score,570,36,24,0xffffeb77);text(real,"LIVES "+lives,770,36,24,Color.WHITE);
 button(real,25,410,115,520,"<");button(real,145,410,235,520,">");button(real,780,410,930,520,"JUMP");
 if(won||dead){rect(real,180,145,780,360,0xee142344);text(real,won?"YOU WIN!":"GAME OVER",285,235,55,0xffffdf6d);text(real,"TAP TO RESTART",320,310,30,Color.WHITE);}
 real.restore();postInvalidateDelayed(16);}
 void button(Canvas c,float l,float t,float r,float b,String s){color(0xaa172f61);c.drawRoundRect(l,t,r,b,22,22,p);text(c,s,l+(r-l)/2-(s.length()*12),t+69,31,Color.WHITE);}
 @Override public boolean onTouchEvent(MotionEvent e){float scale=Math.min(getWidth()/W,getHeight()/H),ox=(getWidth()-W*scale)/2,oy=(getHeight()-H*scale)/2;int action=e.getActionMasked();
 if((won||dead)&&action==MotionEvent.ACTION_DOWN){level=0;lives=3;score=0;load();return true;}
 left=right=false;boolean wantJump=false;for(int i=0;i<e.getPointerCount();i++){if(action==MotionEvent.ACTION_POINTER_UP&&i==e.getActionIndex())continue;float tx=(e.getX(i)-ox)/scale,ty=(e.getY(i)-oy)/scale;if(ty>390){if(tx<135)left=true;else if(tx<250)right=true;else if(tx>740)wantJump=true;}}
 if(wantJump)jump=true;if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){left=right=false;}return true;}
 }
}