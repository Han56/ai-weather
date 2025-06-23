Page({
  data: {
    // 表单数据
    formData: {
      gender: "",
      age: "",
      country: "",
      race: "",
      height: "",
      weight: "",
      styles: [], // 穿衣风格数组
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
    countryIndex: 0,
    raceIndex: 0,
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

    countryOptions: [
      { label: "中国", value: "China" },
      { label: "美国", value: "USA" },
      { label: "韩国", value: "Korea" },
      { label: "英国", value: "UK" },
      { label: "法国", value: "France" },
      { label: "德国", value: "Germany" },
      { label: "澳大利亚", value: "Australia" },
      { label: "加拿大", value: "Canada" },
      { label: "其他", value: "China" },
    ],

    raceOptions: [
      { label: "黄种人", value: "Yellow race" },
      { label: "白种人", value: "caucasian" },
      { label: "黑种人", value: "african" },
      { label: "混血", value: "mixed" },
      { label: "其他", value: "other" },
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
      { label: "40-50kg", value: "40 to 50 kilograms", range: "40-50" },
      { label: "50-60kg", value: "50 to 60 kilograms", range: "50-60" },
      { label: "60-70kg", value: "60 to 70 kilograms", range: "60-70" },
      { label: "70-80kg", value: "70 to 80 kilograms", range: "70-80" },
      { label: "80-90kg", value: "80 to 90 kilograms", range: "80-90" },
      { label: "90kg以上", value: "over 90 kilograms", range: "90+" },
    ],

    // 女性体重选项
    femaleWeightOptions: [
      { label: "35-45kg", value: "35 to 45 kilograms", range: "35-45" },
      { label: "45-55kg", value: "45 to 55 kilograms", range: "45-55" },
      { label: "55-65kg", value: "55 to 65 kilograms", range: "55-65" },
      { label: "65-75kg", value: "65-75 kilograms", range: "65-75" },
      { label: "75-85kg", value: "75 to 85 kilograms", range: "75-85" },
      { label: "85kg以上", value: "over 85 kilograms", range: "85+" },
    ],

    // 穿衣风格选项
    styleOptions: [
      { value: 'casual', label: '休闲风', index: 0 },
      { value: 'business', label: '商务风', index: 1 },
      { value: 'street', label: '街头风', index: 2 },
      { value: 'sport', label: '运动风', index: 3 },
      { value: 'fashion', label: '时尚风', index: 4 },
      { value: 'minimalist', label: '简约风', index: 5 },
      { value: 'vintage', label: '复古风', index: 6 },
      { value: 'sweet', label: '甜美风', index: 7 },
      { value: 'punk', label: '朋克风', index: 8 },
      { value: 'pastoral', label: '田园风', index: 9 }
    ],

    // 饰品选项
    accessoryOptions: [
      { value: 'glasses', label: '眼镜', index: 0 },
      { value: 'sunglasses', label: '太阳镜', index: 1 },
      { value: 'earrings', label: '耳环', index: 2 },
      { value: 'necklace', label: '项链', index: 3 },
      { value: 'bracelet', label: '手链', index: 4 },
      { value: 'ring', label: '戒指', index: 5 },
      { value: 'watch', label: '手表', index: 6 },
      { value: 'hat', label: '帽子', index: 7 },
      { value: 'scarf', label: '围巾', index: 8 },
      { value: 'brooch', label: '胸针', index: 9 }
    ],

    // 发型选项
    hairstyleOptions: [
      { label: "短发", value: "short" },
      { label: "长发", value: "long" },
      { label: "直发", value: "straight" },
      { label: "卷发", value: "curly" },
      { label: "马尾", value: "ponytail" },
      { label: "丸子头", value: "bun" },
      { label: "波浪卷", value: "wavy" },
      { label: "刘海", value: "bangs" },
      { label: "中分", value: "middle_part" },
      { label: "侧分", value: "side_part" },
    ],

    // 当前身高体重选项（根据性别动态变化）
    currentHeightOptions: [],
    currentWeightOptions: [],
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
      url: `http://127.0.0.1:8084/api/potraitSettingInfo/${openId}`,
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
                country: "",
                race: "",
                height: "",
                weight: "",
                styles: [],
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
            country: data.countryRegion || "",
            race: data.ethnicity || "",
            height: data.heightRange || "",
            weight: data.weightRange || "",
            styles: data.clothingStyle ? data.clothingStyle.split(',').filter(Boolean) : [],
            accessories: data.accessoriesPreference ? data.accessoriesPreference.split(',').filter(Boolean) : [],
            hairstyles: data.hairstylePreference ? [data.hairstylePreference] : [],
          }

          // 选中状态
          let styleStates = {}
          formData.styles.forEach(val => { styleStates[val] = true })
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
            console.log('当前styles:', this.data.formData.styles)
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

    // 更新国家索引
    const countryIndex = this.data.countryOptions.findIndex((item) => item.value === formData.country)
    if (countryIndex !== -1) this.setData({ countryIndex })

    // 更新人种索引
    const raceIndex = this.data.raceOptions.findIndex((item) => item.value === formData.race)
    if (raceIndex !== -1) this.setData({ raceIndex })

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

  // 国家选择
  onCountryChange(e) {
    const index = e.detail.value
    this.setData({
      countryIndex: index,
      "formData.country": this.data.countryOptions[index].value,
    }, () => {
      this.checkFormModified()
    })
  },

  // 人种选择
  onRaceChange(e) {
    const index = e.detail.value
    this.setData({
      raceIndex: index,
      "formData.race": this.data.raceOptions[index].value,
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
    
    // 更新选中状态
    const newSelectedStates = {
      ...this.data.selectedStates,
      styles: {
        ...this.data.selectedStates.styles,
        [value]: !this.data.selectedStates.styles[value]
      }
    };
    
    // 更新styles数组
    let styles = [...this.data.formData.styles];
    if (newSelectedStates.styles[value]) {
      if (!styles.includes(value)) {
        styles.push(value);
      }
    } else {
      styles = styles.filter(item => item !== value);
    }
    
    console.log('更新后的styles数组:', styles);
    console.log('更新后的选中状态:', newSelectedStates.styles);
    
    this.setData({
      'formData.styles': styles,
      'selectedStates': newSelectedStates
    }, () => {
      console.log('设置后的styles:', this.data.formData.styles);
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
      })
    } else if (gender === "female") {
      this.setData({
        currentHeightOptions: this.data.femaleHeightOptions,
        currentWeightOptions: this.data.femaleWeightOptions,
      })
    } else {
      this.setData({
        currentHeightOptions: [],
        currentWeightOptions: [],
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
    if (!formData.country) {
      wx.showToast({ title: "请选择国家与地区", icon: "none" })
      return
    }
    if (!formData.race) {
      wx.showToast({ title: "请选择人种", icon: "none" })
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
      clothingStyle: formData.styles.join(','),
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
    const url = 'http://127.0.0.1:8084/api/potraitSettingInfo'
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
