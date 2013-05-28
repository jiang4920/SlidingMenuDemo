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

	private GestureDetector mGestureDetector;// ����

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

	private boolean hasMeasured = false;// �Ƿ�Measured.
	private int window_width;// ��Ļ�Ŀ��
	/** ÿ���Զ�չ��/�����ķ�Χ */
	private int MAX_WIDTH = 0;

	/***
	 * ��ȡ�ƶ����� �ƶ��ľ�����ʵ����mMenuLayout�Ŀ��
	 */
	void getMAX_WIDTH() {
		ViewTreeObserver viewTreeObserver = mMainLayout.getViewTreeObserver();
		// ��ȡ�ؼ����
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
					// ע�⣺ ����mMainLayout�Ŀ�ȡ���ֹ�����ƶ���ʱ��ؼ�����ѹ
					layoutParams.width = window_width;
					mMainLayout.setLayoutParams(layoutParams);

					// ����mMenusLayout�ĳ�ʼλ��.
					layoutParams_1.leftMargin = window_width;
					mMenusLayout.setLayoutParams(layoutParams_1);
					// ע�⣺����lv_set�Ŀ�ȷ�ֹ�����ƶ���ʱ��ؼ�����ѹ
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
	private float mScrollX; // ���黬������

	void doScrolling(float distanceX) {
		isScrolling = true;
		mScrollX += distanceX;// distanceX:����Ϊ������Ϊ��

		Log.v(TAG,"distanceX: "+ distanceX);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
				.getLayoutParams();
		RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) mMenusLayout
				.getLayoutParams();
		layoutParams.leftMargin -= mScrollX;
		layoutParams_1.leftMargin = window_width + layoutParams.leftMargin;
		if (layoutParams.leftMargin >= 0) {
			isScrolling = false;// �Ϲ�ͷ�˲���Ҫ��ִ��AsynMove��
			layoutParams.leftMargin = 0;
			layoutParams_1.leftMargin = window_width;

		} else if (layoutParams.leftMargin <= -MAX_WIDTH) {
			// �Ϲ�ͷ�˲���Ҫ��ִ��AsynMove��
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
	 * �������� ����һ�����ƶ�������һ����. distanceX=�����x-ǰ���x���������0��˵���������ǰ�����ұ߼����һ���
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
		// ����Ĳ���layout_left
		if (view != null && view == mHandeView) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			// ���ƶ�
			if (layoutParams.leftMargin >= 0) {
				new AsynMove().execute(-SPEED);
			} else {
				// ���ƶ�
				new AsynMove().execute(SPEED);
			}
		} else if (view != null && view == mMainLayout) {
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			if (layoutParams.leftMargin < 0) {
				// ˵��layout_left�����ƶ������״̬�����ʱ��������layout_leftӦ��ֱ������ԭ��״̬.(�����Ի�)
				// ���ƶ�
				new AsynMove().execute(SPEED);
			}
		}

		return true;
	}

	private View view = null;// �����view

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		view = v;// ��¼����Ŀؼ�

		// �ɿ���ʱ��Ҫ�жϣ������������Ļλ��������ȥ��
		if (MotionEvent.ACTION_UP == event.getAction() && isScrolling == true) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainLayout
					.getLayoutParams();
			// ����ȥ
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
			if (MAX_WIDTH % Math.abs(params[0]) == 0)// ����
				times = MAX_WIDTH / Math.abs(params[0]);
			else
				times = MAX_WIDTH / Math.abs(params[0]) + 1;// ������

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
			// ���ƶ�
			if (values[0] > 0) {
				layoutParams.leftMargin = Math.min(layoutParams.leftMargin
						+ values[0], 0);
				layoutParams_1.leftMargin = Math.min(layoutParams_1.leftMargin
						+ values[0], window_width);
				Log.v(TAG, "layout_left��" + layoutParams.leftMargin
						+ ",layout_right��" + layoutParams_1.leftMargin);
			} else {
				// ���ƶ�
				layoutParams.leftMargin = Math.max(layoutParams.leftMargin
						+ values[0], -MAX_WIDTH);
				layoutParams_1.leftMargin = Math.max(layoutParams_1.leftMargin
						+ values[0], window_width - MAX_WIDTH);
				Log.v(TAG, "layout_left��" + layoutParams.leftMargin
						+ ",layout_right��" + layoutParams_1.leftMargin);
			}
			mMenusLayout.setLayoutParams(layoutParams_1);
			mMainLayout.setLayoutParams(layoutParams);

		}

	}

}
