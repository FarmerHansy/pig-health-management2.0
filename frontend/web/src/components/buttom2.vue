<template>
  <footer class="footer">
    <div class="container">
      <div class="section">
        <h3>友情链接</h3>
        <ul>
          <li><a href="http://120.nxin.com" target="_blank">猪病通</a></li>
          <li><a href="http://veterinary.scau.xk.hnlat.com/" target="_blank">华南农业大学动物医学院</a></li>
          <li><a href="http://www.hvri.ac.cn" target="_blank">中国农业科学院哈尔滨兽医研究所</a></li>
        </ul>
      </div>

      <div class="section">
        <h3>联系我们</h3>
        <ul>
          <li>地址：中国 · 北京</li>
          <li>官网：www.smartpig.com</li>
          <li>客服：400-888-6666</li>
        </ul>
      </div>

      <div class="section">
        <h3>快速导航</h3>
        <ul>
          <li><a href="#">疾病百科</a></li>
          <li><a href="#">药品中心</a></li>
          <li><a href="#">智能问诊</a></li>
          <li><a href="#">资讯中心</a></li>
        </ul>
      </div>

      <div class="section">
        <h3>关于我们</h3>
        <ul>
          <li>AI 智能诊疗 · 科学防控猪病</li>
          <li>专业的猪健康管理平台</li>
        </ul>
      </div>
    </div>
    <div class="bottom-text">© 2025 Smart Pig 智慧生猪诊疗系统</div>
  </footer>
</template>

<style scoped>
.footer {
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  color: #666;
  padding: 60px 0 30px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 40px;
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
}

.section h3 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #1a1a1a;
  position: relative;
  font-weight: 600;
}
.section h3::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 0;
  width: 40px;
  height: 2px;
  background: #10b981;
  border-radius: 2px;
}

.section ul {
  list-style: none;
  padding: 0;
}
.section ul li {
  margin: 10px 0;
  font-size: 14px;
  color: #666;
  transition: all 0.2s ease;
  line-height: 1.6;
}
.section ul li:hover {
  color: #10b981;
  transform: translateX(3px);
}
.section ul li a {
  color: #666;
  text-decoration: none;
  transition: color 0.2s ease;
}
.section ul li a:hover {
  color: #10b981;
}

.bottom-text {
  text-align: center;
  font-size: 13px;
  color: #999;
  margin-top: 40px;
  padding-top: 25px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
  .container {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
  .section h3 {
    font-size: 16px;
  }
}
@media (max-width: 500px) {
  .container {
    grid-template-columns: 1fr;
  }
  .bottom-text {
    font-size: 12px;
  }
}
</style>

