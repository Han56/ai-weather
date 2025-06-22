package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.ToString;

import java.util.List;

public class AiClothingRecommendationsResponse extends ToString {

    private String imgUrl;

    @JSONField(name = "clothing_info")
    private ClothingInfo clothingInfo;

    @JSONField(name = "detailed_recommendation")
    private DetailedRecommendation detailedRecommendation;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ClothingInfo getClothingInfo() {
        return clothingInfo;
    }

    public void setClothingInfo(ClothingInfo clothingInfo) {
        this.clothingInfo = clothingInfo;
    }

    public DetailedRecommendation getDetailedRecommendation() {
        return detailedRecommendation;
    }

    public void setDetailedRecommendation(DetailedRecommendation detailedRecommendation) {
        this.detailedRecommendation = detailedRecommendation;
    }

    public static class ClothingInfo extends ToString {
        private String top;
        private String bottom;
        private String shoes;
        private List<String> accessories;
        private String background;

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public String getBottom() {
            return bottom;
        }

        public void setBottom(String bottom) {
            this.bottom = bottom;
        }

        public String getShoes() {
            return shoes;
        }

        public void setShoes(String shoes) {
            this.shoes = shoes;
        }

        public List<String> getAccessories() {
            return accessories;
        }

        public void setAccessories(List<String> accessories) {
            this.accessories = accessories;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }
    }

    public static class DetailedRecommendation extends ToString {
        private String title;
        private String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
