package com.xxnan.flowlayout.view;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FlowLayout extends ViewGroup {

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p)
	{
		return new MarginLayoutParams(p);
	}

	@Override
	public MarginLayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams()
	{
		return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}
	/**
	 * ������view�Ĵ�С
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//��ȡ���
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		//��ȡ�߶�
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		//���mode
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		//�߶�mode
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		//ʵ�ʵĿ��
		int width = 0;
		//ʵ�ʵĸ߶�
		int height = 0;
		// ÿһ�еĿ��
		int lineWidth = 0;
		//ÿһ�еĸ߶�
		int lineHeight = 0;
		//viewgroup��view�ĸ���
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams pLayoutParams = (MarginLayoutParams) child
					.getLayoutParams();
			// ÿ����view�Ŀ��
			int childWidth = child.getMeasuredWidth() + pLayoutParams.leftMargin
					+ pLayoutParams.rightMargin;
			int childHeight = child.getMeasuredHeight() + pLayoutParams.topMargin
					+ pLayoutParams.bottomMargin;
			// ���ȴ�����Ļ�ĳ�����һ�и߶ȼ���ÿ�еĸ߶ȣ����ȡ���
			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, sizeWidth);
				height += lineHeight;
				// �µ�һ�п�ʼ
				lineWidth = childWidth;
				lineHeight = childHeight;
			} else {
				lineWidth += childWidth;
				//ȡview�ĸ߶ȵ����ֵ����ÿ����߶���ߵ�Ϊÿ�еĸ߶ȣ�
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// ��������һ�����򽫵�ǰ��¼������Ⱥ͵�ǰlineWidth���Ƚ�  
            if (i == childCount - 1)  
            {  
                width = Math.max(width, lineWidth);  
                height += lineHeight;  
            }  
		}
		setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? sizeWidth
				: width, heightMode == MeasureSpec.EXACTLY ? sizeHeight
				: height);
	}

	// �����е�view�ļ���
	private List<List<View>> allView = new ArrayList<List<View>>();
	// ÿ�е����߶�
	private List<Integer> heightList = new ArrayList<Integer>();

	/**
	 * ��view����
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		allView.clear();
		heightList.clear();
		int width = getWidth();
		int lineWidth = 0;
		int lineHeight = 0;
		int count = getChildCount();
		List<View> lineViews = new ArrayList<View>();
		for (int i = 0; i < count; i++) {
			View childView = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) childView
					.getLayoutParams();
			int childWidth = childView.getMeasuredWidth()+ lp.leftMargin + lp.rightMargin;
			int childHeight = childView.getMeasuredHeight() + lp.bottomMargin
			+ lp.topMargin;
			// ��Ҫ����
			if (lineWidth + childWidth  > width) {
				allView.add(lineViews);
				heightList.add(lineHeight);
				lineWidth = 0;
				lineViews = new ArrayList<View>();
			}
			lineWidth +=childWidth;
			lineHeight = Math.max(lineHeight, childHeight);
			lineViews.add(childView);
		}
		// ��¼���һ��  
        heightList.add(lineHeight);  
        allView.add(lineViews);  
		int left = 0;
		int top = 0;
		int countViews = allView.size();
		for (int i = 0; i < countViews; i++) {
			lineViews = allView.get(i);
			lineHeight = heightList.get(i);
			int lineViewsCount = lineViews.size();
			for (int j = 0; j < lineViewsCount; j++) {
				View childView = getChildAt(j);
				MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView
						.getLayoutParams();
				if (childView.getVisibility() == View.GONE) {
					continue;
				}
				int lc = left + marginLayoutParams.leftMargin;
				int tc = top + marginLayoutParams.topMargin;
				int rc = lc + childView.getMeasuredWidth();
				int bc = tc + childView.getMeasuredHeight();
				childView.layout(lc, tc, rc, bc);
				left += childView.getMeasuredWidth()
						+ marginLayoutParams.leftMargin
						+ marginLayoutParams.rightMargin;

			}
			 left = 0;  
	         top += lineHeight;  
		}
	}
}
