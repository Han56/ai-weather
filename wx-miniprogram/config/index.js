import { getEnv } from './env.js';
import dev from './dev.js';
import pro from './pro.js';

const env = getEnv();
let config = pro;
if (env === 'dev') {
  config = dev;
}else if(env === 'pro'){
  config = pro
}


export default config;