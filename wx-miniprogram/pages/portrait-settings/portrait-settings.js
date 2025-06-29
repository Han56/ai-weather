const { baseUrl } = require('../../utils/config');

Page({
  data: {
    // 表单数据
    formData: {
      gender: "",
      age: "",
      country: "China", // 默认设置为 China
      race: "Yellow race", // 默认设置为 Yellow race
      height: "",
      weight: "",
      style: "", // 改为单选
      accessories: [], // 饰品数组
      hairstyles: [], // 发型数组
    },
    // 选中状态对象
    selectedStates: {
      styles: {},
      accessories: {}
    },
    // 原始数据，用于比较是否有修改
    originalData: null,
    // 是否已修改
    isModified: false,
    // 选择器索引
    ageIndex: 0,
    heightIndex: 0,
    weightIndex: 0,

    // 基本选项
    genderOptions: [
      { label: "男", value: "male" },
      { label: "女", value: "female" },
    ],

    ageOptions: [
      { label: "7-12岁", value: "child,7 to 12 years old", range: "7-12" },
      { label: "13-18岁", value: "teenager,aged 13 to 18", range: "13-18" },
      { label: "21-30岁", value: "adult,21-30 years old", range: "21-30" },
      { label: "31-40岁", value: "adult,aged 31 to 40,", range: "31-40" },
      { label: "41-65岁", value: "middle,aged 41 to 65", range: "41-65" },
      { label: "65岁以上", value: "senior", range: "65+" },
    ],

    // 男性身高选项
    maleHeightOptions: [
      { label: "150厘米以下", value: "Less than 150 centimeters", range: "<150" },
      { label: "150-160厘米", value: "150-160 centimeters", range: "150-160" },
      { label: "160-170厘米", value: "160-170 centimeters", range: "160-170" },
      { label: "170-180厘米", value: "170-180 centimeters", range: "170-180" },
      { label: "180-190厘米", value: "180-190 centimeters", range: "180-190" },
      { label: "190厘米以上", value: "More than 190 centimeters", range: "190+" },
    ],

    // 女性身高选项
    femaleHeightOptions: [
      { label: "140厘米以下", value: "Less than 140 centimeters", range: "<140" },
      { label: "140-150厘米", value: "140-150 centimeters", range: "140-150" },
      { label: "150-160厘米", value: "150-160 centimeters", range: "150-160" },
      { label: "160-170厘米", value: "160-170 centimeters", range: "160-170" },
      { label: "170-180厘米", value: "170-180 centimeters", range: "170-180" },
      { label: "180厘米以上", value: "More than 180 centimeters", range: "180+" },
    ],

    // 男性体重选项
    maleWeightOptions: [
      { label: "40-50kg", value: "40 to 50 kilograms,Very thin", range: "40-50" },
      { label: "50-60kg", value: "50 to 60 kilograms,Thin and slender", range: "50-60" },
      { label: "60-70kg", value: "60 to 70 kilograms,Slim body", range: "60-70" },
      { label: "70-80kg", value: "70 to 80 kilograms,Slightly fat and thin", range: "70-80" },
      { label: "80-90kg", value: "80 to 90 kilograms,Slightly plump", range: "80-90" },
      { label: "90kg以上", value: "over 90 kilograms,Obesity", range: "90+" },
    ],

    // 女性体重选项
    femaleWeightOptions: [
      { label: "35-45kg", value: "35 to 45 kilograms,Very thin", range: "35-45" },
      { label: "45-55kg", value: "45 to 55 kilograms,Thin and slender", range: "45-55" },
      { label: "55-65kg", value: "55 to 65 kilograms,Slim body", range: "55-65" },
      { label: "65-75kg", value: "65-75 kilograms,Slightly fat and thin", range: "65-75" },
      { label: "75-85kg", value: "75 to 85 kilograms,Slightly plump", range: "75-85" },
      { label: "85kg以上", value: "over 85 kilograms,Obesity", range: "85+" },
    ],

    // 穿衣风格选项 - 男性
    maleStyleOptions: [
      { value: 'Natural and healing dressing style', label: '自然治愈风' },
      { value: 'Zero-pressure commuting style', label: '零压通勤风' },
      { value: 'Urban light wild dressing style', label: '城市轻野风' },
      { value: 'Retro dressing style', label: '复古主义' },
      { value: 'Fashion sportswear style', label: '时尚运动风' },
      { value: 'Oversized suit dressing style', label: '超大号西装风' },
      { value: 'Soft and light-colored dressing style', label: '柔美浅色风' },
      { value: 'High-end workwear style', label: '高级工装风' },
      { value: 'Office dressing style', label: '办公职场风' }
    ],

    // 穿衣风格选项 - 女性
    femaleStyleOptions: [
      { value: 'Soft and light-colored dressing style', label: '柔美浅色风' },
      { value: 'Natural healing dressing style', label: '自然治愈风' },
      { value: 'Elegant commuting style', label: '优雅通勤风' },
      { value: 'French elegant dressing style', label: '法式优雅风' },
      { value: 'Retro literary dressing style', label: '复古文艺风' },
      { value: 'Simple minimalist dressing style', label: '简约极简风' },
      { value: 'Sweet girl dressing style', label: '甜美少女风' },
      { value: 'Fashion sportswear style', label: '时尚运动风' }
    ],

    // 饰品选项 - 男性
    maleAccessoryOptions: [
      { value: 'Business myopia glasses without a lower frame', label: '眼镜(偏商务)' },
      { value: 'Korean version of gentle and intellectual black-framed square-framed glasses for both men and women', label: '韩系方框黑框眼镜' },
      { value: 'Men\'s necklace', label: '项链' },
      { value: 'Men\'s bracelet', label: '手链' },
      { value: 'Men\'s NBA sports bracelet', label: '手环' },
      { value: 'Men\'s elegant ring', label: '戒指' },
      { value: 'Super cool sunglasses for men', label: '太阳镜' },
      { value: 'Super cool and super business watch for men', label: '手表(偏商务)' },
      { value: 'Men\'s sports watch', label: '运动手表' },
      { value: 'Men\'s sun hat', label: '帽子' },
      { value: 'Men\'s super business handbag', label: '手提包' }
    ],

    // 饰品选项 - 女性
    femaleAccessoryOptions: [
      { value: 'Business myopia glasses without a lower frame', label: '眼镜(偏商务)' },
      { value: 'Korean version of gentle and intellectual black-framed square-framed glasses for both men and women', label: '韩系方框黑框眼镜' },
      { value: 'A super beautiful necklace for ladies', label: '项链' },
      { value: 'Super beautiful earrings for ladies', label: '耳饰' },
      { value: 'A super beautiful and elegant bracelet for ladies', label: '手链' },
      { value: 'A super beautiful, elegant and luxurious ring for ladies', label: '戒指' },
      { value: 'Super cool sunglasses for female', label: '太阳镜' },
      { value: 'A super beautiful and elegant hat for ladies', label: '帽子' },
      { value: 'A super beautiful and elegant bag for ladies', label: '包包' }
    ],

    // 当前饰品选项（根据性别动态变化）
    currentAccessoryOptions: [],

    // 发型选项 - 男性
    maleHairstyleOptions: [
      { label: "极简寸头", value: "American crew cut hairstyle" },
      { label: "简约毛寸", value: "Hair inch hairstyle" },
      { label: "韩系中分", value: "Korean-style split hairstyle" },
      { label: "韩式自然纹理", value: "Korean-style natural texture hairstyle" },
      { label: "侧向纹理", value: "Korean-style side-textured hairstyle" },
      { label: "嘻哈脏辫", value: "Hip-hop black braided hairstyle" },
      { label: "港风侧背", value: "Hong Kong-style side-back hairstyle" },
      { label: "动感前刺", value: "Dynamic front spike hairstyle" }
    ],

    // 发型选项 - 女性
    femaleHairstyleOptions: [
      { label: "韩系自然卷", value: "Korean-style natural curly hairstyle" },
      { label: "韩系内扣", value: "Korean-style inward-cursed hairstyle" },
      { label: "法式波浪", value: "French wavy hairstyle" },
      { label: "法式蓬松刘海", value: "French fluffy bangs hairstyle" },
      { label: "日系甜美短发", value: "A sweet short Japanese-style hairstyle" },
      { label: "日系空气卷发", value: "Japanese-style air curly hairstyle" },
      { label: "经典波波头", value: "Classic Bob hairstyle" }
    ],

    // 当前风格选项（根据性别动态变化）
    currentStyleOptions: [],

    // 当前身高体重选项（根据性别动态变化）
    currentHeightOptions: [],
    currentWeightOptions: [],

    // 当前发型选项（根据性别动态变化）
    currentHairstyleOptions: [],
  },

  onLoad() {
    // 页面加载时获取数据
    this.loadUserProfile()
    this.updateHeightWeightOptions()
  },

  // 加载用户画像数据
  loadUserProfile() {
    const app = getApp()
    const openId = app.globalData.openId

    if (!openId) {
      wx.showToast({
        title: '用户信息不完整',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '加载中...',
      mask: true
    })

    // 调用后端接口获取数据
    wx.request({
      url: `${baseUrl}/api/potraitSettingInfo/${openId}`,
      method: 'GET',
      success: (res) => {
        if (res.statusCode === 200) {
          const data = res.data
          console.log('后端返回数据：', data)
          
          // 如果数据为空，设置isModified为false
          if (!data) {
            this.setData({
              formData: {
                gender: "",
                age: "",
                country: "China",
                race: "Yellow race",
                height: "",
                weight: "",
                style: "",
                accessories: [],
                hairstyles: []
              },
              selectedStates: {
                styles: {},
                accessories: {}
              },
              isModified: false,
              originalData: null
            })
            return
          }

          // 直接用英文 value 回填
          const formData = {
            gender: data.gender || "",
            age: data.ageGroup || "",
            country: data.countryRegion || "China",
            race: data.ethnicity || "Yellow race",
            height: data.heightRange || "",
            weight: data.weightRange || "",
            style: data.clothingStyle || "",
            accessories: data.accessoriesPreference ? data.accessoriesPreference.split(',').filter(Boolean) : [],
            hairstyles: data.hairstylePreference ? [data.hairstylePreference] : [],
          }

          // 选中状态
          let styleStates = {}
          styleStates[formData.style] = true
          let accessoryStates = {}
          formData.accessories.forEach(val => { accessoryStates[val] = true })

          this.setData({
            formData,
            selectedStates: {
              styles: styleStates,
              accessories: accessoryStates
            },
            originalData: JSON.stringify(formData),
            isModified: false
          }, () => {
            this.updateHeightWeightOptions()
            this.updatePickerIndexes()
            console.log('当前style:', this.data.formData.style)
            console.log('当前accessories:', this.data.formData.accessories)
            console.log('当前选中状态:', this.data.selectedStates)
          })
        }
      },
      fail: (err) => {
        console.error('获取用户画像失败:', err)
        wx.showToast({
          title: '获取数据失败',
          icon: 'none'
        })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  // 检查表单是否被修改
  checkFormModified() {
    const currentData = JSON.stringify(this.data.formData)
    const isModified = currentData !== this.data.originalData
    this.setData({ isModified })
  },

  // 更新选择器索引
  updatePickerIndexes() {
    const { formData } = this.data

    // 更新年龄索引
    const ageIndex = this.data.ageOptions.findIndex((item) => item.value === formData.age)
    if (ageIndex !== -1) this.setData({ ageIndex })

    // 更新身高体重索引
    this.updateHeightWeightIndexes()
  },

  // 性别选择
  onGenderChange(e) {
    const gender = e.currentTarget.dataset.value
    this.setData({
      "formData.gender": gender,
      "formData.height": "", // 重置身高
      "formData.weight": "", // 重置体重
      "formData.style": "", // 重置风格
      "formData.hairstyles": [], // 重置发型
      "formData.accessories": [], // 重置饰品
      heightIndex: 0,
      weightIndex: 0,
    }, () => {
      this.updateHeightWeightOptions()
      this.checkFormModified()
    })
  },

  // 年龄选择
  onAgeChange(e) {
    const index = e.detail.value
    this.setData({
      ageIndex: index,
      "formData.age": this.data.ageOptions[index].value,
    }, () => {
      this.checkFormModified()
    })
  },

  // 身高选择
  onHeightChange(e) {
    const index = e.detail.value
    this.setData({
      heightIndex: index,
      "formData.height": this.data.currentHeightOptions[index].value,
    }, () => {
      this.checkFormModified()
    })
  },

  // 体重选择
  onWeightChange(e) {
    const index = e.detail.value
    this.setData({
      weightIndex: index,
      "formData.weight": this.data.currentWeightOptions[index].value,
    }, () => {
      this.checkFormModified()
    })
  },

  // 穿衣风格选择
  onStyleTap(e) {
    const value = e.currentTarget.dataset.value;
    const label = e.currentTarget.dataset.label;
    console.log('点击的风格值:', value, '标签:', label);
    
    // 更新选中状态 - 单选逻辑
    const newSelectedStates = {
      ...this.data.selectedStates,
      styles: {}
    };
    newSelectedStates.styles[value] = true;
    
    this.setData({
      'formData.style': value,
      'selectedStates': newSelectedStates
    }, () => {
      console.log('设置后的style:', this.data.formData.style);
      console.log('设置后的选中状态:', this.data.selectedStates.styles);
      this.checkFormModified();
    });
  },

  // 饰品选择
  onAccessoryTap(e) {
    const value = e.currentTarget.dataset.value;
    const label = e.currentTarget.dataset.label;
    console.log('点击的饰品值:', value, '标签:', label);
    
    // 更新选中状态
    const newSelectedStates = {
      ...this.data.selectedStates,
      accessories: {
        ...this.data.selectedStates.accessories,
        [value]: !this.data.selectedStates.accessories[value]
      }
    };
    
    // 更新accessories数组
    let accessories = [...this.data.formData.accessories];
    if (newSelectedStates.accessories[value]) {
      if (!accessories.includes(value)) {
        accessories.push(value);
      }
    } else {
      accessories = accessories.filter(item => item !== value);
    }
    
    console.log('更新后的accessories数组:', accessories);
    console.log('更新后的选中状态:', newSelectedStates.accessories);
    
    this.setData({
      'formData.accessories': accessories,
      'selectedStates': newSelectedStates
    }, () => {
      console.log('设置后的accessories:', this.data.formData.accessories);
      console.log('设置后的选中状态:', this.data.selectedStates.accessories);
      this.checkFormModified();
    });
  },

  // 发型选择
  onHairstyleChange(e) {
    const value = e.currentTarget.dataset.value
    this.setData({
      'formData.hairstyles': [value]
    }, () => {
      this.checkFormModified()
    })
  },

  // 更新身高体重选项
  updateHeightWeightOptions() {
    const { gender } = this.data.formData

    if (gender === "male") {
      this.setData({
        currentHeightOptions: this.data.maleHeightOptions,
        currentWeightOptions: this.data.maleWeightOptions,
        currentStyleOptions: this.data.maleStyleOptions,
        currentHairstyleOptions: this.data.maleHairstyleOptions,
        currentAccessoryOptions: this.data.maleAccessoryOptions,
      })
    } else if (gender === "female") {
      this.setData({
        currentHeightOptions: this.data.femaleHeightOptions,
        currentWeightOptions: this.data.femaleWeightOptions,
        currentStyleOptions: this.data.femaleStyleOptions,
        currentHairstyleOptions: this.data.femaleHairstyleOptions,
        currentAccessoryOptions: this.data.femaleAccessoryOptions,
      })
    } else {
      this.setData({
        currentHeightOptions: [],
        currentWeightOptions: [],
        currentStyleOptions: [],
        currentHairstyleOptions: [],
        currentAccessoryOptions: [],
      })
    }

    this.updateHeightWeightIndexes()
  },

  // 更新身高体重索引
  updateHeightWeightIndexes() {
    const { formData, currentHeightOptions, currentWeightOptions } = this.data

    // 更新身高索引
    const heightIndex = currentHeightOptions.findIndex((item) => item.value === formData.height)
    if (heightIndex !== -1) this.setData({ heightIndex })

    // 更新体重索引
    const weightIndex = currentWeightOptions.findIndex((item) => item.value === formData.weight)
    if (weightIndex !== -1) this.setData({ weightIndex })
  },

  // 表单提交
  onFormSubmit(e) {
    const { formData, originalData } = this.data
    const app = getApp()

    // 验证必填项
    if (!formData.gender) {
      wx.showToast({ title: "请选择性别", icon: "none" })
      return
    }
    if (!formData.age) {
      wx.showToast({ title: "请选择年龄段", icon: "none" })
      return
    }
    if (!formData.height) {
      wx.showToast({ title: "请选择身高范围", icon: "none" })
      return
    }
    if (!formData.weight) {
      wx.showToast({ title: "请选择体重范围", icon: "none" })
      return
    }
    if (!formData.style) {
      wx.showToast({ title: "请选择穿衣风格", icon: "none" })
      return
    }
    if (!formData.hairstyles.length) {
      wx.showToast({ title: "请选择发型", icon: "none" })
      return
    }

    // 获取全局变量
    const openId = app.globalData.openId
    const adCode = app.globalData.adcode

    if (!openId || !adCode) {
      wx.showToast({ title: "用户信息不完整", icon: "none" })
      return
    }

    // 直接用 value 组装请求数据
    const requestData = {
      openId: openId,
      gender: formData.gender,
      ageGroup: formData.age,
      countryRegion: formData.country,
      ethnicity: formData.race,
      heightRange: formData.height,
      weightRange: formData.weight,
      clothingStyle: formData.style,
      accessoriesPreference: formData.accessories.join(','),
      hairstylePreference: formData.hairstyles.length > 0 ? formData.hairstyles[0] : '',
      adCode: adCode
    }

    // 显示加载提示
    wx.showLoading({
      title: '保存中...',
      mask: true
    })

    // 根据originalData是否存在决定调用新增还是更新接口
    const url = `${baseUrl}/api/potraitSettingInfo`
    const method = originalData ? 'PUT' : 'POST'

    console.log('提交方法：', method)
    console.log('提交数据：', requestData)

    // 调用后端接口
    wx.request({
      url: url,
      method: method,
      data: requestData,
      success: (res) => {
        if (res.statusCode === 200) {
          // 显示成功提示
          wx.showToast({
            title: "保存成功",
            icon: "success",
            duration: 2000,
          })

          // 更新原始数据
          this.setData({
            originalData: JSON.stringify(formData),
            isModified: false
          })

          // 设置全局标记，通知AI穿搭页面需要重置
          app.globalData.portraitUpdated = true

          // 延迟返回上一页
          setTimeout(() => {
            wx.navigateBack()
          }, 2000)
        } else {
          wx.showToast({
            title: "保存失败，请重试",
            icon: "none"
          })
        }
      },
      fail: (err) => {
        console.error('保存失败:', err)
        wx.showToast({
          title: "网络错误，请重试",
          icon: "none"
        })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  // 返回上一页
  onBack() {
    wx.navigateBack({
      delta: 1
    })
  },
})
