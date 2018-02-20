#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

void main(void) {
  // Grouping texcoord variables in order to make it work in the GMA 950. See post #13
  // in this thread:
  // http://www.idevgames.com/forums/thread-3467.html
  vec2 tc = vertTexCoord.st;
  vec3 color = texture2D(texture, tc).xyz;
  
  vec3 hsv = rgb2hsv(color);
  //vec3 fcolor = vec3(0., 0., mix(0., 1., step(.9,color.b / (color.r+color.g))));
  //vec3 ocolor = mix(color, fcolor, floor(mod(u_time, 2.)));
  //vec3 ocolor = vec3(mix(0., 1., step(abs(.666666-hsv.x),0.1)));
  //ocolor = vec3(mix(0., ocolor.x, step(abs(hsv.y),.99)));
  //ocolor = vec3(mix(0., ocolor.x, step(.2,abs(hsv.y))));
  //ocolor = clamp(ocolor, .1, 1.) * color;
  
  
  vec3 ocolor = vec3(mix(vec3(0.), vec3(0.,0.,1.), step(abs(.666666-hsv.x),0.1)*step(abs(hsv.y),1.)*step(.05,abs(hsv.y))));
  float s = step(0.,abs(hsv.y))*step(abs(hsv.y),.3);
  float v = step(.4,abs(hsv.z))*step(abs(hsv.z),.6);
  if(ocolor != vec3(0.,0.,1.)) ocolor = vec3(s*v, 0., 0.);
  gl_FragColor = vec4(ocolor,1.0);
}
