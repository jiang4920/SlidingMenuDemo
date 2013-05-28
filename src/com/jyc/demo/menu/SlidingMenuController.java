package com.jyc.demo.menu;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.RelativeLayout;

public class SlidingMenuController implements OnTouchListener,
		GestureDetector.OnGestureListener {

	private static final int SPEED = 30;

	private String TAG = "JYC_MENU";

	private GestureDetector mGestureDetector;// 手势

	private View mMenusLayout, mMainLayout, mHandeView;
	private Activity mActvity;

	public SlidingMenuController(Activity actvity, View handleView,
			View menusLayout, View mainLayout) {
		mMenusLayout = menusLayout;
		mMainLayout = mainLayout;
		mHandeView = handleView;
		mActvity = actvity;
		mHandeView.setOnTouchListener(this);
		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setIsLongpressEnabled(false);
		getMAX_WIDTH();
	}

	public void setDragEnable(boolean isDragEnable) {

	}

	private boolean hasMeasured = false;// 是否Measured.
	private int window_width;// 屏幕的宽度
	/** 每次自动展开/收缩的范围 */
	private int MAX_WIDTH = 0;

	/***
	 * 获取移动距离 移动的距离其实就是mMenuLayout的宽度
	 */
	void getMAX_WIDTH() {
		ViewTreeObserver viewTreeObserver = mMainLayout.getViewTreeObserver();
		// 获取控件宽度
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_width = mActvity.getWindowManager()
							.getDefaultDisplay().getWidth();
					MAX_WIDTH = mMenusLayout.getWidth();
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
							.getLayoutParams();
					RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) mMenusLayout
							.getLayoutParams();
					// ViewGroup.LayoutParams layoutParams_2 = mylaout
					// .getLayoutParams();
					// 注意： 设置mMainLayout的宽度。防止被在移动的时候控件被挤压
					layoutParams.width = window_width;
					mMainLayout.setLayoutParams(layoutParams);

					// 设置mMenusLayout的初始位置.
					layoutParams_1.leftMargin = window_width;
					mMenusLayout.setLayoutParams(layoutParams_1);
					// 注意：设置lv_set的宽度防止被在移动的时候控件被挤压
					// layoutParams_2.width = MAX_WIDTH;
					// mylaout.setLayoutParams(layoutParams_2);

					Log.v(TAG, "MAX_WIDTH=" + MAX_WIDTH + "width="
							+ window_width);
					hasMeasured = true;
				}
				return true;
			}
		});

	}

	private boolean isScrolling = false;
	private float mScrollX; // 滑块滑动距离

	void doScrolling(float distanceX) {
		isScrolling = true;
		mScrollX += distanceX;// distanceX:向左为正，右为负

		Log.v(TAG,"distanceX: "+ distanceX);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
				.getLayoutParams();
		RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) mMenusLayout
				.getLayoutParams();
		layoutParams.leftMargin -= mScrollX;
		layoutParams_1.leftMargin = window_width + layoutParams.leftMargin;
		if (layoutParams.leftMargin >= 0) {
			isScrolling = false;// 拖过头了不需要再执行AsynMove了
			layoutParams.leftMargin = 0;
			layoutParams_1.leftMargin = window_width;

		} else if (layoutParams.leftMargin <= -MAX_WIDTH) {
			// 拖过头了不需要再执行AsynMove了
			isScrolling = false;
			layoutParams.leftMargin = -MAX_WIDTH;
			layoutParams_1.leftMargin = window_width - MAX_WIDTH;
		}
		Log.v(TAG, "layoutParams.leftMargin=" + layoutParams.leftMargin
				+ ",layoutParams_1.leftMargin =" + layoutParams_1.leftMargin);

		mMainLayout.setLayoutParams(layoutParams);
		mMenusLayout.setLayoutParams(layoutParams_1);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		mScrollX = 0;
		isScrolling = false;
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	/***
	 * 滑动监听 就是一个点移动到另外一个点. distanceX=后面点x-前面点x，如果大于0，说明后面点在前面点的右边及向右滑动
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		doScrolling(distanceX);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// 点击的不是layout_left
		if (view != null && view == mHandeView) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			// 左移动
			if (layoutParams.leftMargin >= 0) {
				new AsynMove().execute(-SPEED);
			} else {
				// 右移动
				new AsynMove().execute(SPEED);
			}
		} else if (view != null && view == mMainLayout) {
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			if (layoutParams.leftMargin < 0) {
				// 说明layout_left处于移动最左端状态，这个时候如果点击layout_left应该直接所以原有状态.(更人性化)
				// 右移动
				new AsynMove().execute(SPEED);
			}
		}

		return true;
	}

	private View view = null;// 点击的view

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		view = v;// 记录点击的控件

		// 松开的时候要判断，如果不到半屏幕位子则缩回去，
		if (MotionEvent.ACTION_UP == event.getAction() && isScrolling == true) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			// 缩回去
			if (layoutParams.leftMargin < -window_width / 2) {
				new AsynMove().execute(-SPEED);
			} else {
				new AsynMove().execute(SPEED);
			}
		}

		return mGestureDetector.onTouchEvent(event);
	}

	private final static int sleep_time = 5;

	class AsynMove extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int times = 0;
			if (MAX_WIDTH % Math.abs(params[0]) == 0)// 整除
				times = MAX_WIDTH / Math.abs(params[0]);
			else
				times = MAX_WIDTH / Math.abs(params[0]) + 1;// 有余数

			for (int i = 0; i < times; i++) {
				publishProgress(params[0]);
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		/**
		 * update UI
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) mMenusLayout
					.getLayoutParams();
			// 右移动
			if (values[0] > 0) {
				layoutParams.leftMargin = Math.min(layoutParams.leftMargin
						+ values[0], 0);
				layoutParams_1.leftMargin = Math.min(layoutParams_1.leftMargin
						+ values[0], window_width);
				Log.v(TAG, "layout_left右" + layoutParams.leftMargin
						+ ",layout_right右" + layoutParams_1.leftMargin);
			} else {
				// 左移动
				layoutParams.leftMargin = Math.max(layoutParams.leftMargin
						+ values[0], -MAX_WIDTH);
				layoutParams_1.leftMargin = Math.max(layoutParams_1.leftMargin
						+ values[0], window_width - MAX_WIDTH);
				Log.v(TAG, "layout_left左" + layoutParams.leftMargin
						+ ",layout_right左" + layoutParams_1.leftMargin);
			}
			mMenusLayout.setLayoutParams(layoutParams_1);
			mMainLayout.setLayoutParams(layoutParams);

		}

	}

}
