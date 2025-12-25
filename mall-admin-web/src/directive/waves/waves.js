import './waves.css'

const context = '@@wavesContext'

function isWindow(obj) {
  return obj != null && obj === obj.window
}

function getWindow(elem) {
  return isWindow(elem) ? elem : elem.nodeType === 9 && elem.defaultView
}

function offset(elem) {
  var docElem, win,
    box = { top: 0, left: 0 },
    doc = elem && elem.ownerDocument

  docElem = doc.documentElement

  if (typeof elem.getBoundingClientRect !== typeof undefined) {
    box = elem.getBoundingClientRect()
  }
  win = getWindow(doc)
  return {
    top: box.top + win.pageYOffset - docElem.clientTop,
    left: box.left + win.pageXOffset - docElem.clientLeft
  }
}

function convertStyle(obj) {
  var style = ''

  for (var a in obj) {
    if (obj.hasOwnProperty(a)) {
      style += (a + ':' + obj[a] + ';')
    }
  }

  return style
}

const WavesEffect = {
  // Effect delay
  duration: 750,

  show: function(e, element) {
    // Disable right click
    if (e.button === 2) {
      return false
    }

    var el = element || this
    var ripple = document.createElement('div')
    ripple.className = 'waves-ripple'
    el.appendChild(ripple)
    var pos = offset(el)
    var relativeY = (e.pageY - pos.top)
    var relativeX = (e.pageX - pos.left)
    var scale = 'scale(' + ((el.clientWidth / 100) * 10) + ')'

    if ('touches' in e) {
      relativeY = (e.touches[0].pageY - pos.top)
      relativeX = (e.touches[0].pageX - pos.left)
    }

    ripple.setAttribute('data-hold', Date.now())
    ripple.setAttribute('data-scale', scale)
    ripple.setAttribute('data-x', relativeX)
    ripple.setAttribute('data-y', relativeY)

    var rippleStyle = {
      'top': relativeY + 'px',
      'left': relativeX + 'px'
    }

    ripple.className = ripple.className + ' waves-notransition'
    ripple.setAttribute('style', convertStyle(rippleStyle))
    ripple.className = ripple.className.replace('waves-notransition', '')

    rippleStyle['-webkit-transform'] = scale
    rippleStyle['-moz-transform'] = scale
    rippleStyle['-ms-transform'] = scale
    rippleStyle['-o-transform'] = scale
    rippleStyle.transform = scale
    rippleStyle.opacity = '1'

    rippleStyle['-webkit-transition-duration'] = WavesEffect.duration + 'ms'
    rippleStyle['-moz-transition-duration'] = WavesEffect.duration + 'ms'
    rippleStyle['-o-transition-duration'] = WavesEffect.duration + 'ms'
    rippleStyle['transition-duration'] = WavesEffect.duration + 'ms'

    rippleStyle['-webkit-transition-timing-function'] = 'cubic-bezier(0.250, 0.460, 0.450, 0.940)'
    rippleStyle['-moz-transition-timing-function'] = 'cubic-bezier(0.250, 0.460, 0.450, 0.940)'
    rippleStyle['-o-transition-timing-function'] = 'cubic-bezier(0.250, 0.460, 0.450, 0.940)'
    rippleStyle['transition-timing-function'] = 'cubic-bezier(0.250, 0.460, 0.450, 0.940)'

    ripple.setAttribute('style', convertStyle(rippleStyle))
  },

  hide: function(e) {
    var el = this
    var width = el.clientWidth * 1.4

    var ripples = el.querySelectorAll('.waves-ripple')
    if (ripples.length === 0) {
      return false
    }
    var ripple = ripples[ripples.length - 1]
    var relativeX = ripple.getAttribute('data-x')
    var relativeY = ripple.getAttribute('data-y')
    var scale = ripple.getAttribute('data-scale')

    var diff = Date.now() - Number(ripple.getAttribute('data-hold'))
    var delay = 350 - diff

    if (delay < 0) {
      delay = 0
    }

    setTimeout(function() {
      var style = {
        'top': relativeY + 'px',
        'left': relativeX + 'px',
        'opacity': '0',

        '-webkit-transition-duration': WavesEffect.duration + 'ms',
        '-moz-transition-duration': WavesEffect.duration + 'ms',
        '-o-transition-duration': WavesEffect.duration + 'ms',
        'transition-duration': WavesEffect.duration + 'ms',
        '-webkit-transform': scale,
        '-moz-transform': scale,
        '-ms-transform': scale,
        '-o-transform': scale,
        'transform': scale
      }

      ripple.setAttribute('style', convertStyle(style))

      setTimeout(function() {
        try {
          el.removeChild(ripple)
        } catch (e) {
          return false
        }
      }, WavesEffect.duration)
    }, delay)
  },

  wrapInput: function(elements) {
    for (var a = 0; a < elements.length; a++) {
      var el = elements[a]

      if (el.tagName.toLowerCase() === 'input') {
        var parent = el.parentNode

        if (parent.tagName.toLowerCase() === 'i' && parent.className.indexOf('waves-effect') !== -1) {
          continue
        }

        var wrapper = document.createElement('i')
        wrapper.className = el.className + ' waves-input-wrapper'

        var elementStyle = el.getAttribute('style')

        if (!elementStyle) {
          elementStyle = ''
        }

        wrapper.setAttribute('style', elementStyle)

        el.className = 'waves-button-input'
        el.removeAttribute('style')

        parent.replaceChild(wrapper, el)
        wrapper.appendChild(el)
      }
    }
  }
}

const Waves = {
  displayEffect: function(options) {
    options = options || {}

    if ('duration' in options) {
      WavesEffect.duration = options.duration
    }

    WavesEffect.wrapInput($$('.waves-effect'))

    Array.prototype.forEach.call($$('.waves-effect'), function(i) {
      i.addEventListener('click', WavesEffect.show, false)
      i.addEventListener('mouseup', WavesEffect.hide, false)
      i.addEventListener('mouseleave', WavesEffect.hide, false)
    })
  },

  attach: function(element) {
    if (element.tagName.toLowerCase() === 'input') {
      WavesEffect.wrapInput([element])
      element = element.parentElement
    }

    if (element.addEventListener) {
      element.addEventListener('click', WavesEffect.show, false)
      element.addEventListener('mouseup', WavesEffect.hide, false)
      element.addEventListener('mouseleave', WavesEffect.hide, false)
    }
  },

  calm: function(element) {
    if (element.removeEventListener) {
      element.removeEventListener('click', WavesEffect.show)
      element.removeEventListener('mouseup', WavesEffect.hide)
      element.removeEventListener('mouseleave', WavesEffect.hide)
    }
  }
}

function $$(selector) {
  return document.querySelectorAll(selector)
}

export default {
  bind(el, binding) {
    el.addEventListener('click', WavesEffect.show, false)
    el.addEventListener('mouseup', WavesEffect.hide, false)
    el.addEventListener('mouseleave', WavesEffect.hide, false)
    el[context] = {
      removeClick: el.removeEventListener.bind(el, 'click', WavesEffect.show, false),
      removeMouseup: el.removeEventListener.bind(el, 'mouseup', WavesEffect.hide, false),
      removeMouseleave: el.removeEventListener.bind(el, 'mouseleave', WavesEffect.hide, false)
    }
  },

  unbind(el) {
    el[context].removeClick()
    el[context].removeMouseup()
    el[context].removeMouseleave()
    el[context] = null
    delete el[context]
  }
}
