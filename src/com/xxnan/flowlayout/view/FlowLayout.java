package com.xxnan.flowlayout.view;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
/**
 * time: 2016/9/3
 * @author xxnan
 *
 */
public class FlowLayout extends ViewGroup {

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	 * 计算子view的大小
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//获取宽度
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		//获取高度
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		//宽度mode
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		//高度mode
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		//实际的宽度
		int width = 0;
		//实际的高度(每一行累加)
		int height = 0;
		// 每一行的宽度
		int lineWidth = 0;
		//每一行的高度
		int lineHeight = 0;
		//viewgroup子view的个数
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams pLayoutParams = (MarginLayoutParams) child
					.getLayoutParams();
			// 每个子view的宽高
			int childWidth = child.getMeasuredWidth() + pLayoutParams.leftMargin
					+ pLayoutParams.rightMargin;
			int childHeight = child.getMeasuredHeight() + pLayoutParams.topMargin
					+ pLayoutParams.bottomMargin;
			// 长度大于屏幕的长度则换一行，高度加上每行的高度，宽度取大的
			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, childWidth);
				height += lineHeight;
				// 新的一行开始
				lineWidth = childWidth;
				lineHeight = childHeight;
			} else {
				lineWidth += childWidth;
				//取view的高度的最大值，（每行里高度最高的为每行的高度）
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较  
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

	// 所有行的view的集合
	private List<List<View>> allView = new ArrayList<List<View>>();
	// 每行的最大高度
	private List<Integer> heightList = new ArrayList<Integer>();

	/**
	 * 子view布局
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		allView.clear();
		heightList.clear();
		//屏幕的宽度
		int width = getWidth();
		//每一行的宽度
		int lineWidth = 0;
		//每一行的高度
		int lineHeight = 0;
		//总的子view的个数
		int count = getChildCount();
		List<View> lineViews = new ArrayList<View>();
		for (int i = 0; i < count; i++) {
			//获取每一个子view
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child
					.getLayoutParams();
			//子view的宽度
			int childWidth = child.getMeasuredWidth();
			//子view的高度
			int childHeight = child.getMeasuredHeight();                    
			// 需要换行
			if (lineWidth + childWidth+ lp.leftMargin + lp.rightMargin  > width) {
				allView.add(lineViews);
				heightList.add(lineHeight);
				//新的一行开始
				lineWidth = 0;
				lineViews = new ArrayList<View>();
			}
			lineWidth +=childWidth+ lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight+ lp.topMargin + lp.bottomMargin);
			//view添加到每一行的集合里面
			lineViews.add(child);
		}
		// 记录最后一行  
        heightList.add(lineHeight);  
        allView.add(lineViews);  
        
        //每个view的左边距离和离顶部距离
		int left = 0;
		int top = 0;
		int countViews = allView.size();
		for (int i = 0; i < countViews; i++) {
			//每一行的view的集合
			lineViews = allView.get(i);
			//每一行的高度
			lineHeight = heightList.get(i);
			int lineViewsCount = lineViews.size();
			for (int j = 0; j < lineViewsCount; j++) {
				//遍历每一行的子view并布局
				View childView = getChildAt(j);
				MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView
						.getLayoutParams();
				//如果子view的Visibility属性==GONE跳过这个view
				if (childView.getVisibility() == View.GONE) {
					continue;
				}
				//计算每个view的left,top,right,buttom
				int lc = left + marginLayoutParams.leftMargin;
				int tc = top + marginLayoutParams.topMargin;
				int rc = lc + childView.getMeasuredWidth();
				int bc = tc + childView.getMeasuredHeight();
				//布局
				childView.layout(lc, tc, rc, bc);
				//每一行的开始left都要加上前面的view的width
				left += childView.getMeasuredWidth()
						+ marginLayoutParams.leftMargin
						+ marginLayoutParams.rightMargin;

			}
			//一行结束left重置为0
			//高度则加上上一行的高度
			 left = 0;  
	         top += lineHeight;  
		}
	}
}
