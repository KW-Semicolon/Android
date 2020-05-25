package dc24.iqos.breather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DateRecyclerAdapter extends RecyclerView.Adapter<DateRecyclerAdapter.DateItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<SmokData> listData = new ArrayList<>();

    @NonNull
    @Override
    public DateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(SmokData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class DateItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mon;
        private TextView day1;
        private TextView day2;
        private TextView time;

        DateItemViewHolder(View itemView) {
            super(itemView);

            mon = itemView.findViewById(R.id.date1);
            day1 = itemView.findViewById(R.id.date2);
            day2 = itemView.findViewById(R.id.date3);
            time = itemView.findViewById(R.id.date4);
        }

        void onBind(SmokData date) {
            mon.setText(date.getMonth());
            day1.setText(date.getDay());
            day2.setText(date.getDayoftheweek());
            time.setText(date.getTime());
        }
    }
}