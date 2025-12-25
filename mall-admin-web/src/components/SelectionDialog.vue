<template>
  <el-dialog :title="title" :visible.sync="visible" width="600px" @close="$emit('cancel')">
    <el-input v-model="keyword" placeholder="搜索" size="small" @keyup.enter="load" style="margin-bottom:8px" />
    <el-table :data="list" height="400" @selection-change="selChange" :row-key="row=>row.id" :default-selection="selected">
      <el-table-column type="selection" width="55" v-if="multiple" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
    </el-table>
    <span slot="footer">
      <el-button @click="visible=false">取消</el-button>
      <el-button type="primary" @click="confirm">确定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { fetchSimpleProducts, fetchSimpleCoupons } from '@/api/common'
export default {
  props:{ type:{type:String,required:true}, multiple:{type:Boolean,default:true}, value:{type:Array,default:()=>[]} },
  data(){return{visible:true,keyword:'',list:[],selected:[]}},
  computed:{title(){return this.type==='product'?'选择商品':'选择优惠券'}},
  created(){this.selected=[...this.value];this.load();},
  methods:{
    load(){
      const api=this.type==='product'?fetchSimpleProducts:fetchSimpleCoupons;
      api({keyword:this.keyword,limit:50}).then(res=>{if(res.code===200)this.list=res.data});
    },
    selChange(rows){this.selected=rows;},
    confirm(){this.$emit('confirm',this.selected);this.visible=false;}
  }
}
</script> 