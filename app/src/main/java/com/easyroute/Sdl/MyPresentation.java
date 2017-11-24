package com.easyroute.Sdl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyroute.R;
import com.easyroute.content.constant.RouteType;
import com.easyroute.content.model.CalculatedRoute;
import com.easyroute.ui.activity.MainActivity;
import com.easyroute.ui.fragment.RouteFragment;
import com.easyroute.ui.view.RouteOptionTabLayout;
import com.google.android.gms.maps.CameraUpdateFactory;

import static java.lang.Thread.sleep;

/**
 * Created by hp on 6.07.2017.
 */

public class MyPresentation extends VirtualDisplayEncoder.SdlPresentation {

    static Drawable drawable2;
    public static ImageView imageView;
    static float firstX, firstY, lastX, lastY, changeX, changeY;
    private LinearLayout hizliBackground,rahatBackground,ekonomikBackground;
    private TextView tvTitle2, tvTitle3, tvTitle4;
    private TextView tvTravelTime2, tvTravelTime3,tvTravelTime4;
    private TextView tvDistance2, tvDistance3, tvDistance4;
    private TextView tvCost2, tvCost3, tvCost4;
    private LinearLayout llCostRow2 , llCostRow3, llCostRow4;

    ViewGroup.MarginLayoutParams marginHizli;
    ViewGroup.MarginLayoutParams marginRahat;
    ViewGroup.MarginLayoutParams marginEkonomik;


    public MyPresentation(Context context, Display display) {
        super(context, display);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asd);
        hizliBackground = (LinearLayout)findViewById(R.id.hizliBackground);
        ekonomikBackground = (LinearLayout)findViewById(R.id.ekonomikBacground);
        rahatBackground = (LinearLayout)findViewById(R.id.rahatBacground);
        llCostRow2 = (LinearLayout)findViewById(R.id.llCostRow2);
        llCostRow3 = (LinearLayout)findViewById(R.id.llCostRow3);
        llCostRow4 = (LinearLayout)findViewById(R.id.llCostRow4);

        tvTitle2 = (TextView)findViewById(R.id.tvTitle2);
        tvTitle3 = (TextView)findViewById(R.id.tvTitle3);
        tvTitle4 = (TextView)findViewById(R.id.tvTitle4);
        tvTravelTime2 = (TextView)findViewById(R.id.tvTravelTime2);
        tvTravelTime3 = (TextView)findViewById(R.id.tvTravelTime3);
        tvTravelTime4 = (TextView)findViewById(R.id.tvTravelTime4);
        tvDistance2 = (TextView)findViewById(R.id.tvDistance2);
        tvDistance3 = (TextView)findViewById(R.id.tvDistance3);
        tvDistance4 = (TextView)findViewById(R.id.tvDistance4);
        tvCost2 = (TextView)findViewById(R.id.tvCost2);
        tvCost3 = (TextView)findViewById(R.id.tvCost3);
        tvCost4 = (TextView)findViewById(R.id.tvCost4);

        hizliBackground.setClickable(true);
        rahatBackground.setClickable(true);
        ekonomikBackground.setClickable(true);

        hizliBackground.setOnClickListener(customOnClickListener);
        rahatBackground.setOnClickListener(customOnClickListener);
        ekonomikBackground.setOnClickListener(customOnClickListener);

        marginHizli = (ViewGroup.MarginLayoutParams) hizliBackground.getLayoutParams();
        marginRahat = (ViewGroup.MarginLayoutParams) rahatBackground.getLayoutParams();
        marginEkonomik = (ViewGroup.MarginLayoutParams) ekonomikBackground.getLayoutParams();



        firstX =0;firstY =0;lastX=0;lastY=0;changeX =0; changeY =0;
        imageView = (ImageView)findViewById(R.id.imageView2);
        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                while (true) {
                    AsyncMapview asyncMapview = new AsyncMapview();
                    asyncMapview.execute();



                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        myThread.start();

        Thread myThread2 = new Thread(new Runnable(){
            @Override
            public void run()
            {
                while (true) {
                    AsyncButtons asyncButtons = new AsyncButtons();
                    asyncButtons.execute();



                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        myThread2.start();


    }

    View.OnClickListener customOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            LinearLayout linearLayout = (LinearLayout)v;
            int id = linearLayout.getId();

            if(id == R.id.hizliBackground){
                RouteOptionTabLayout.mSelectedRoute = RouteOptionTabLayout.rovFast.getRoute();
                RouteOptionTabLayout.mListener.onRouteOptionTabItemSelectListener(RouteOptionTabLayout.rovFast.getRoute());
                RouteOptionTabLayout.setSelectedRoute(RouteOptionTabLayout.rovFast.getRoute());
            }else if(id == R.id.rahatBacground) {
                RouteOptionTabLayout.mSelectedRoute = RouteOptionTabLayout.rovRelax.getRoute();
                RouteOptionTabLayout.mListener.onRouteOptionTabItemSelectListener(RouteOptionTabLayout.rovRelax.getRoute());
                RouteOptionTabLayout.setSelectedRoute(RouteOptionTabLayout.rovRelax.getRoute());
            }else if(id == R.id.ekonomikBacground) {
                RouteOptionTabLayout.mSelectedRoute = RouteOptionTabLayout.rovEconomic.getRoute();
                RouteOptionTabLayout.mListener.onRouteOptionTabItemSelectListener(RouteOptionTabLayout.rovEconomic.getRoute());
                RouteOptionTabLayout.setSelectedRoute(RouteOptionTabLayout.rovEconomic.getRoute());
            }
        }
    };



  private class AsyncMapview extends AsyncTask<Void, Void, Void>
  {

      @Override
      protected void onPreExecute() {
          super.onPreExecute();
      }
      @Override
      protected Void doInBackground(Void... params) {
          return null;
      }

      @Override
      protected void onPostExecute(Void result) {
          super.onPostExecute(result);
          MainActivity.mapView.getMap().moveCamera(CameraUpdateFactory.scrollBy((firstX-lastX)/30,(firstY-lastY)/10));
          firstX =0;firstY =0;lastX=0;lastY=0;changeX=0;changeY=0;
          drawable2 = MainActivity.getDrawable();
          if (drawable2 != null && imageView!= null) {
              imageView.setImageDrawable(drawable2);
          }



      }

  }
    private class AsyncButtons extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setButtons();
        }

    }

  private void setButtons(){
      if (RouteOptionTabLayout.rovFast != null ) {
          hizliBackground.setBackgroundColor(RouteOptionTabLayout.rovFast.getmRouteType().getColor());
          if (hizliBackground.getVisibility() == View.GONE) {
              hizliBackground.setVisibility(View.VISIBLE);
          }
          setRouteType(tvTitle2,RouteOptionTabLayout.rovFast.getmRouteType() );
          tvTravelTime2.setText(RouteOptionTabLayout.rovFast.getRoute().getRoute().getTravelTimeMinutes() + " dk");
          tvDistance2.setText(RouteOptionTabLayout.rovFast.getRoute().getRoute().getTotalDistance() + " km");
          setCost(RouteOptionTabLayout.rovFast.getRoute(), llCostRow2, tvCost2);

          if (RouteOptionTabLayout.mSelectedRoute == RouteOptionTabLayout.rovFast.getRoute()) {

              marginHizli.setMargins(0, 0, 20, 0);
              hizliBackground.requestLayout();
          } else {
              marginHizli.setMargins(0, 0, 0, 0);
              hizliBackground.requestLayout();
          }

      } else
          hizliBackground.setVisibility(View.GONE);

      if (RouteOptionTabLayout.rovEconomic != null ){
          ekonomikBackground.setBackgroundColor(RouteOptionTabLayout.rovEconomic.getmRouteType().getColor());
          if (ekonomikBackground.getVisibility() == View.GONE) {
              ekonomikBackground.setVisibility(View.VISIBLE);
          }
          setRouteType(tvTitle3, RouteOptionTabLayout.rovEconomic.getmRouteType());
          tvTravelTime3.setText(RouteOptionTabLayout.rovEconomic.getRoute().getRoute().getTravelTimeMinutes() + " dk");
          tvDistance3.setText(RouteOptionTabLayout.rovEconomic.getRoute().getRoute().getTotalDistance() + " km");
          setCost(RouteOptionTabLayout.rovEconomic.getRoute(), llCostRow3, tvCost3);

          if (RouteOptionTabLayout.mSelectedRoute == RouteOptionTabLayout.rovEconomic.getRoute()) {
              marginEkonomik.setMargins(0, 0, 20, 0);
              ekonomikBackground.requestLayout();
          } else {
              marginEkonomik.setMargins(0, 0, 0, 0);
              ekonomikBackground.requestLayout();
          }
      } else
          ekonomikBackground.setVisibility(View.GONE);

      if (RouteOptionTabLayout.rovRelax != null){
          rahatBackground.setBackgroundColor(RouteOptionTabLayout.rovRelax.getmRouteType().getColor());
          if (rahatBackground.getVisibility() == View.GONE) {
              rahatBackground.setVisibility(View.VISIBLE);
          }
          setRouteType(tvTitle4, RouteOptionTabLayout.rovRelax.getmRouteType());
          tvTravelTime4.setText(RouteOptionTabLayout.rovRelax.getRoute().getRoute().getTravelTimeMinutes() + " dk");
          tvDistance4.setText(RouteOptionTabLayout.rovRelax.getRoute().getRoute().getTotalDistance() + " km");
          setCost(RouteOptionTabLayout.rovRelax.getRoute(), llCostRow4, tvCost4);
          if (RouteOptionTabLayout.mSelectedRoute == RouteOptionTabLayout.rovRelax.getRoute()) {
              marginRahat.setMargins(0, 0, 20, 0);
              rahatBackground.requestLayout();
          } else {
              marginRahat.setMargins(0, 0, 0, 0);
              rahatBackground.requestLayout();
          }
      }else
          rahatBackground.setVisibility(View.GONE);


  }

    private void setRouteType(TextView tvTitle, RouteType mRouteType) {
        if (mRouteType == RouteType.FAST) {
            tvTitle.setText(R.string.route_fragment_fast_route_label);
        } else if (mRouteType == RouteType.ECONOMIC) {
            tvTitle.setText(R.string.route_fragment_economic_route_label);
        } else if (mRouteType == RouteType.RELAX) {
            tvTitle.setText(R.string.route_fragment_relax_route_label);
        } else if (mRouteType == RouteType.ALL) {
            tvTitle.setText(R.string.route_fragment_single_route_label);
        } else if (mRouteType == RouteType.FAST_ECONOMIC) {
            tvTitle.setText(R.string.route_fragment_fast_economic_route_label);
        } else if (mRouteType == RouteType.FAST_RELAX) {
            tvTitle.setText(R.string.route_fragment_fast_relax_route_label);
        } else if (mRouteType == RouteType.ECONOMIC_RELAX) {
            tvTitle.setText(R.string.route_fragment_economic_relax_route_label);
        }
    }

    private void setCost(CalculatedRoute mRoute, LinearLayout llCostRow, TextView tvCost) {
        if (RouteFragment.mVehicleInfo == null) {
            llCostRow.setVisibility(View.GONE);
        } else {
            llCostRow.setVisibility(View.VISIBLE);
            tvCost.setText(mRoute.getTotalCostString() + " ₺");
        }
    }



    public static void touchEvent() {
        firstX = AppLinkService.firstX;
        firstY = AppLinkService.firstY;
        lastY = AppLinkService.lastY;
        lastX = AppLinkService.lastX;
        changeX = AppLinkService.changeX;
        changeY = AppLinkService.changeY;

    }

}