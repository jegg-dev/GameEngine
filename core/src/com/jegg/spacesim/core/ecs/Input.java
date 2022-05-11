package com.jegg.spacesim.core.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Input implements InputProcessor {

    public class InputState{
        public boolean pressed, down, released;
    }
    public class KeyState extends InputState{
        public int key;
        public KeyState(int key){
            this.key = key;
        }
    }

    public static int A = com.badlogic.gdx.Input.Keys.A;
    public static int B = com.badlogic.gdx.Input.Keys.B;
    public static int C = com.badlogic.gdx.Input.Keys.C;
    public static int D = com.badlogic.gdx.Input.Keys.D;
    public static int E = com.badlogic.gdx.Input.Keys.E;
    public static int F = com.badlogic.gdx.Input.Keys.F;
    public static int G = com.badlogic.gdx.Input.Keys.G;
    public static int H = com.badlogic.gdx.Input.Keys.H;
    public static int I = com.badlogic.gdx.Input.Keys.I;
    public static int J = com.badlogic.gdx.Input.Keys.J;
    public static int K = com.badlogic.gdx.Input.Keys.K;
    public static int L = com.badlogic.gdx.Input.Keys.L;
    public static int M = com.badlogic.gdx.Input.Keys.M;
    public static int N = com.badlogic.gdx.Input.Keys.N;
    public static int O = com.badlogic.gdx.Input.Keys.O;
    public static int P = com.badlogic.gdx.Input.Keys.P;
    public static int Q = com.badlogic.gdx.Input.Keys.Q;
    public static int R = com.badlogic.gdx.Input.Keys.R;
    public static int S = com.badlogic.gdx.Input.Keys.S;
    public static int T = com.badlogic.gdx.Input.Keys.T;
    public static int U = com.badlogic.gdx.Input.Keys.U;
    public static int V = com.badlogic.gdx.Input.Keys.V;
    public static int W = com.badlogic.gdx.Input.Keys.W;
    public static int X = com.badlogic.gdx.Input.Keys.X;
    public static int Y = com.badlogic.gdx.Input.Keys.Y;
    public static int Z = com.badlogic.gdx.Input.Keys.Z;
    public static int LeftShift = com.badlogic.gdx.Input.Keys.SHIFT_LEFT;
    public static int LeftControl = com.badlogic.gdx.Input.Keys.CONTROL_LEFT;
    public static int Tab = com.badlogic.gdx.Input.Keys.TAB;
    public static int Space = com.badlogic.gdx.Input.Keys.SPACE;
    public static int Escape = com.badlogic.gdx.Input.Keys.ESCAPE;
    public static int Num1 = com.badlogic.gdx.Input.Keys.NUM_1;
    public static int Num2 = com.badlogic.gdx.Input.Keys.NUM_2;
    public static int Num3 = com.badlogic.gdx.Input.Keys.NUM_3;
    public static int Num4 = com.badlogic.gdx.Input.Keys.NUM_4;
    public static int Num5 = com.badlogic.gdx.Input.Keys.NUM_5;
    public static int Num6 = com.badlogic.gdx.Input.Keys.NUM_6;
    public static int Num7 = com.badlogic.gdx.Input.Keys.NUM_7;
    public static int Num8 = com.badlogic.gdx.Input.Keys.NUM_8;
    public static int Num9 = com.badlogic.gdx.Input.Keys.NUM_9;
    public static int Num0 = com.badlogic.gdx.Input.Keys.NUM_0;
    public static int Mouse0 = 256;
    public static int Mouse1 = 257;

    public Array<KeyState> keyStates = new Array<>();
    public Array<KeyState> buttonStates = new Array<>();
    public static final Vector2 mousePos = new Vector2();
    public static float scroll;

    public Input(){
        for(int i = 0; i < 256; i++){
            keyStates.add(new KeyState(i));
        }
        for(int i = 0; i < 4; i++){
            buttonStates.add(new KeyState(i));
        }
    }

    @Override
    public boolean keyDown(int keycode){
        keyStates.get(keycode).pressed = true;
        keyStates.get(keycode).down = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode){
        keyStates.get(keycode).down = false;
        keyStates.get(keycode).released = true;
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        buttonStates.get(button).down = true;
        buttonStates.get(button).pressed = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        buttonStates.get(button).down = false;
        buttonStates.get(button).released = true;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mousePos.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mousePos.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scroll = amountY;
        return false;
    }

    public void update(){
        for(int i = 0; i < 256; i++){
            keyStates.get(i).pressed = false;
            keyStates.get(i).released = false;
        }
        for(int i = 0; i < 4; i++){
            buttonStates.get(i).pressed = false;
            buttonStates.get(i).released = false;
        }
        scroll = 0;
    }

    public static boolean getKeyUp(int keycode){
        if(keycode < 256) {
            return ((Input) Gdx.input.getInputProcessor()).keyStates.get(keycode).released;
        }
        else{
            return ((Input) Gdx.input.getInputProcessor()).buttonStates.get(keycode - 256).released;
        }
    }

    public static boolean getKey(int keycode){
        if(keycode < 256) {
            return ((Input) Gdx.input.getInputProcessor()).keyStates.get(keycode).down;
        }
        else{
            return ((Input) Gdx.input.getInputProcessor()).buttonStates.get(keycode - 256).down;
        }
    }

    public static boolean getKeyDown(int keycode){
        if(keycode < 256) {
            return ((Input) Gdx.input.getInputProcessor()).keyStates.get(keycode).pressed;
        }
        else{
            return ((Input) Gdx.input.getInputProcessor()).buttonStates.get(keycode - 256).pressed;
        }
    }
}
