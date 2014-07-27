package ru.lanjusto.busscheduler.android;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @see <a href="http://www.fampennings.nl/maarten/android/09keyboard/index.htm">http://www.fampennings.nl/maarten/android/09keyboard/index.htm</a>
 */
public class RouteNumKeyboard {
    private final Runnable onDone;

    private KeyboardView keyboardView;
    private Activity hostActivity;

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        private final static int CODE_DONE = -2;
        private final static int CODE_DELETE = -1;
        private final static int CODE_CANCEL = -3;

        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            // Get the EditText and its Editable
            final View focusCurrent = hostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null || focusCurrent.getClass() != EditText.class) {
                return;
            }
            final EditText edittext = (EditText) focusCurrent;
            final Editable editable = edittext.getText();
            final int start = edittext.getSelectionStart();
            // Apply the key to the edittext
            switch (primaryCode) {
                case CODE_CANCEL:
                    hideCustomKeyboard();
                    break;
                case CODE_DELETE:
                    if (editable != null && start > 0) {
                        editable.delete(start - 1, start);
                    }
                    break;
                case CODE_DONE:
                    onDone.run();
                    break;
                default:
                    // insert character
                    editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    public RouteNumKeyboard(Activity hostActivity, int viewId, int layoutId, Runnable onDone) {
        this.hostActivity = hostActivity;
        this.onDone = onDone;
        keyboardView = (KeyboardView) this.hostActivity.findViewById(viewId);
        keyboardView.setKeyboard(new Keyboard(this.hostActivity, layoutId));
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
        // Hide the standard keyboard initially
        hostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Returns whether the CustomKeyboard is visible.
     */
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the CustomKeyboard visible, and hide the system keyboard for view v.
     */
    public void showCustomKeyboard(View v) {
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if (v != null) {
            ((InputMethodManager) hostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * Make the CustomKeyboard invisible.
     */
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    /**
     * Register <c>EditText<c> with resource id <c>editTextId</c> (on the hosting activity) for using this custom keyboard.
     *
     * @param editTextId The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(int editTextId) {
        // Find the EditText 'editTextId'
        final EditText edittext = (EditText) hostActivity.findViewById(editTextId);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCustomKeyboard(v);
                } else {
                    hideCustomKeyboard();
                }
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
}
