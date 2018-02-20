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
  /*
  vec2 tc0 = vertTexCoord.st + vec2(-texOffset.s, -texOffset.t);
  vec2 tc1 = vertTexCoord.st + vec2(         0.0, -texOffset.t);
  vec2 tc2 = vertTexCoord.st + vec2(+texOffset.s, -texOffset.t);
  vec2 tc3 = vertTexCoord.st + vec2(-texOffset.s,          0.0);
  vec2 tc4 = vertTexCoord.st + vec2(         0.0,          0.0);
  vec2 tc5 = vertTexCoord.st + vec2(+texOffset.s,          0.0);
  vec2 tc6 = vertTexCoord.st + vec2(-texOffset.s, +texOffset.t);
  vec2 tc7 = vertTexCoord.st + vec2(         0.0, +texOffset.t);
  vec2 tc8 = vertTexCoord.st + vec2(+texOffset.s, +texOffset.t);
  */
  /*
  vec4 col0 = texture2D(texture, tc0);
  vec4 col1 = texture2D(texture, tc1);
  vec4 col2 = texture2D(texture, tc2);
  vec4 col3 = texture2D(texture, tc3);
  vec4 col4 = texture2D(texture, tc4);
  vec4 col5 = texture2D(texture, tc5);
  vec4 col6 = texture2D(texture, tc6);
  vec4 col7 = texture2D(texture, tc7);
  vec4 col8 = texture2D(texture, tc8);
  */
  
  //float o = -8.*texOffset.s;
  
  float o;
  float t = 0.;
  if(texture2D(texture,vertTexCoord.st + vec2(0.,0.)).b>.9) {			//if pixel blue
    
    for(float i=-8.; i<8.; i++) {							//iterate through pixels below it
      o = i*texOffset.s;								
      t += texture2D(texture, vertTexCoord.st + vec2(o, -2.*texOffset.t)).b;	//add blue from all pixels below
    }
    t *= .125;
    //t *= .5;									
    t = 1.-t;									//subtract total blue pixels from 1
    t = step(.75, t);
    t *= texture2D(texture, vertTexCoord.st + vec2(0.,8.*texOffset.t)).b;	//check whether pixel 8 above is blue
    t *= 1.-texture2D(texture, vertTexCoord.st + vec2(0.,-2.*texOffset.t)).b;
    //t = 1.;
  }
  
  //float t = abs((.9-vertTexCoord.t)*256.);
  //if(t > 1.) t = 0.;
  gl_FragColor = vec4(vec3(t), 1.0);// * vertColor;
}
