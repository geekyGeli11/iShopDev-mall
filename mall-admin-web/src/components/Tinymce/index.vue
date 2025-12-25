<template>
  <div class="tinymce-container editor-container">
    <textarea class="tinymce-textarea" :id="tinymceId"></textarea>
    <div class="editor-custom-btn-container">
      <editorImage color="#1890ff" class="editor-upload-btn" @successCBK="imageSuccessCBK"></editorImage>
    </div>
  </div>
</template>

<script>
  import editorImage from './components/editorImage'
  import '../../../static/tinymce4.7.5/langs/zh_CN'

  const plugins = [
 'advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime legacyoutput link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount autoresize'
  ];
  const toolbar = [
    'bold italic underline strikethrough alignleft aligncenter alignright outdent indent blockquote undo redo removeformat code',
    'hr bullist numlist link image charmap preview anchor pagebreak fullscreen insertdatetime media table forecolor backcolor'
  ];
  export default {
    name: 'tinymce',
    components: {editorImage},
    props: {
      id: {
        type: String
      },
      value: {
        type: String,
        default: ''
      },
      toolbar: {
        type: Array,
        required: false,
        default() {
          return []
        }
      },
      menubar: {
        default: 'file edit insert view format table'
      },
      height: {
        type: Number,
        required: false,
        default: 360
      },
      width: {
        type: Number,
        required: false,
        default: 720
      },
      minHeight: {
        type: Number,
        required: false,
        default: 200
      },
      maxHeight: {
        type: Number,
        required: false,
        default: 800
      }
    },
    data() {
      return {
        hasChange: false,
        hasInit: false,
        tinymceId: this.id || 'vue-tinymce-' + +new Date()
      }
    },
    watch: {
      value(val) {
        if (!this.hasChange && this.hasInit) {
          this.$nextTick(() => {
            const editor = window.tinymce.get(this.tinymceId)
            if (editor) {
              editor.setContent(val || '')
            }
          })
        }
      }
    },
    mounted() {
      this.$nextTick(() => {
        this.initTinymce()
      })
    },
    activated() {
      if (!this.hasInit) {
        this.$nextTick(() => {
          this.initTinymce()
        })
      }
    },
    deactivated() {
      this.destroyTinymce()
    },
    methods: {
      initTinymce() {
        const _this = this
        if (window.tinymce) {
          window.tinymce.init({
            selector: `#${this.tinymceId}`,
            width: this.width,
            height: this.height,
            language: 'zh_CN',
            body_class: 'panel-body ',
            object_resizing: false,
            toolbar: this.toolbar.length > 0 ? this.toolbar : toolbar,
            menubar: false,
            plugins: plugins,
            end_container_on_empty_block: true,
            powerpaste_word_import: 'clean',
            code_dialog_height: 450,
            code_dialog_width: 1000,
            advlist_bullet_styles: 'square',
            advlist_number_styles: 'default',
            imagetools_cors_hosts: ['www.tinymce.com', 'codepen.io'],
            default_link_target: '_blank',
            link_title: false,
            branding: false,
            autoresize_min_height: this.minHeight,
            autoresize_max_height: this.maxHeight,
            autoresize_on_init: true,
            autoresize_bottom_margin: 16,
            content_style: `
              body { font-family: Helvetica,Arial,sans-serif; font-size: 14px; }
              img { max-width: 100%; height: auto; display: block; margin: 0 auto; }
              .wscnph { max-width: 100%; height: auto; display: block; margin: 10px auto; }
            `,
            init_instance_callback: editor => {
              if (_this.value) {
                editor.setContent(_this.value)
              }
              _this.hasInit = true
              editor.on('NodeChange Change KeyUp SetContent', () => {
                this.hasChange = true
                this.$emit('input', editor.getContent())
              })
            }
          })
        } else {
          console.warn('TinyMCE尚未加载完成，正在尝试重新初始化')
          setTimeout(() => {
            this.initTinymce()
          }, 200)
        }
      },
      destroyTinymce() {
        if (window.tinymce && window.tinymce.get(this.tinymceId)) {
          window.tinymce.get(this.tinymceId).destroy()
        }
      },
      setContent(value) {
        const editor = window.tinymce.get(this.tinymceId)
        if (editor) {
          editor.setContent(value)
        }
      },
      getContent() {
        const editor = window.tinymce.get(this.tinymceId)
        if (editor) {
          return editor.getContent()
        }
        return ''
      },
      imageSuccessCBK(arr) {
        const _this = this
        const editor = window.tinymce.get(_this.tinymceId)
        if (editor) {
          arr.forEach(v => {
            editor.insertContent(`<img class="wscnph" src="${v.url}" style="max-width: 100%; height: auto; display: block; margin: 10px auto;">`)
          })
        }
      }
    },
    destroyed() {
      this.destroyTinymce()
    }
  }
</script>

<style scoped>
  .tinymce-container {
    position: relative;
    line-height: normal;
  }

  .tinymce-container >>> .mce-fullscreen {
    z-index: 10000;
  }

  .tinymce-textarea {
    visibility: hidden;
    z-index: -1;
  }

  .editor-custom-btn-container {
    position: absolute;
    right: 10px;
    top: 2px;
    z-index: 2005;
  }

  .editor-upload-btn {
    display: inline-block;
  }
</style>

<style>
  /* 全局样式，确保兼容Mac和Windows */
  .mce-panel {
    z-index: 1001 !important;
  }
  
  .mce-top-part {
    position: relative;
  }

  .mce-toolbar-grp {
    z-index: 1002 !important;
  }

  /* 编辑器内图片样式 */
  .mce-content-body img {
    max-width: 100% !important;
    height: auto !important;
    display: block !important;
    margin: 10px auto !important;
  }

  .mce-content-body .wscnph {
    max-width: 100% !important;
    height: auto !important;
    display: block !important;
    margin: 10px auto !important;
  }
</style>
