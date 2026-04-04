<template>
	<view class="layout">
		<view class="header">
			<view class="welcome-section">
				<text class="greeting">👋 欢迎</text>
				<view class="title-wrapper">
					<text class="subtitle">专业的生猪健康管理平台</text>
				</view>
			</view>
		</view>

		<scroll-view class="main-scroll" scroll-y="true">
			<view class="stats-section">
				<view class="stat-item">
					<view class="stat-icon">📊</view>
					<text class="stat-label">疾病案例</text>
				</view>
				<view class="stat-item">
					<view class="stat-icon">🏥</view>
					<text class="stat-label">药品种类</text>
				</view>
				<view class="stat-item">
					<view class="stat-icon">👥</view>
					<text class="stat-label">服务农户</text>
				</view>
			</view>

			<view class="features-section">
				<view class="feature-card" @click="gotoDoctor">
					<view class="card-icon">👨‍⚕️</view>
					<view class="card-content">
						<text class="card-title">AI兽医</text>
						<text class="card-desc">智能诊断,快速准确</text>
					</view>
				</view>

				<view class="feature-card" @click="gotoIllness">
					<view class="card-icon">🔍</view>
					<view class="card-content">
						<text class="card-title">疾病查询</text>
						<text class="card-desc">疾病信息，症状诊断</text>
					</view>
				</view>

				<view class="feature-card" @click="gotoMedecine">
					<view class="card-icon">💊</view>
					<view class="card-content">
						<text class="card-title">药品管理</text>
						<text class="card-desc">用药指导，安全用药</text>
					</view>
				</view>

				<view class="feature-card" @click="gotoKnowledge">
					<view class="card-icon">📚</view>
					<view class="card-content">
						<text class="card-title">知识库</text>
						<text class="card-desc">养殖知识，最新资讯</text>
					</view>
				</view>
			</view>

			<view class="activity-section">
				<view class="section-title-wrapper">
					<text class="section-title">最近活动</text>
					<text class="section-count">{{activityList.length}} 条</text>
				</view>

				<view class="activity-item" v-for="(item,index) in activityList" :key="index">
					<view class="activity-icon-wrapper">
						<view class="activity-icon" :class="`icon-${item.color}`"></view>
					</view>
					<view class="activity-info">
						<text class="activity-text">{{item.text}}</text>
						<text class="activity-time">{{item.time}}</text>
					</view>
				</view>
			</view>

			<view class="footer-space"></view>
		</scroll-view>

		<!-- #ifdef H5 -->
			<TabBar />
		<!-- #endif -->
	</view>
</template>

<script setup>
import TabBar from '@/components/TabBar.vue'
import { ref } from 'vue';

const activityList = ref([
  { text: 'AI诊断：疑似猪瘟症状', time: '2小时前', color: 'green' },
  { text: '查看了青霉素使用说明', time: '1天前', color: 'blue' },
  { text: '阅读了《猪病预防指南》', time: '3天前', color: 'purple' },
]);

const gotoDoctor = () => {
	uni.switchTab({
		url:"/pages/AIDoctor/AIDoctor"
	})
}


const gotoIllness = () => {
	uni.switchTab({
		url:"/pages/SearchIllness/SearchIllness"
	})
}

const gotoKnowledge = () => {
	uni.switchTab({
		url:"/pages/Knowledge/Knowledge"
	})
}

const gotoMedecine = () => {
	uni.navigateTo({
		url:"/pages/medicine/medicine"
	})
}

</script>

<style lang="scss" scoped>
.layout {
	height: 100%;
	background-color: #f5f5f5;

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 40rpx 30rpx 30rpx;
		background: linear-gradient(140deg, #f8f9fa 0%, #e8f5e9 100%);
		position: relative;

		&::before {
			content: '';
			position: absolute;
			bottom: 0;
			left: 0;
			right: 0;
			height: 100rpx;
			background: linear-gradient(to bottom, rgba(232, 245, 233, 0.5) 0%, transparent 100%);
			z-index: 0;
		}

		.welcome-section {
			position: relative;
			z-index: 1;

			.greeting {
				font-size: 28rpx;
				color: #666;
				display: block;
				margin-bottom: 12rpx;
			}

			.title-wrapper {
				display: flex;
				flex-direction: column;

				.main-title {
					font-size: 42rpx;
					font-weight: 700;
					color: #2E7D32;
					line-height: 1.2;
					margin-bottom: 4rpx;
				}

				.subtitle {
					font-size: 24rpx;
					color: #666;
					font-weight: 400;
					opacity: 0.9;
				}
			}
		}

		.actions-section {
			display: flex;
			gap: 24rpx;
			position: relative;
			z-index: 1;

			.icon-box {
				width: 56rpx;
				height: 56rpx;
				background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
				border-radius: 14rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
				position: relative;

				.icon-badge {
					position: absolute;
					top: -6rpx;
					right: -6rpx;
					width: 24rpx;
					height: 24rpx;
					background-color: #FF4444;
					border-radius: 50%;
					display: flex;
					align-items: center;
					justify-content: center;
					box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.2);

					.badge-count {
						font-size: 18rpx;
						color: #fff;
						font-weight: 600;
					}
				}

				&:nth-child(2) {
					background: linear-gradient(135deg, #f8f9fa 0%, #e8f5e9 100%);
				}
			}
		}
	}

	.main-scroll {
		height: calc(100vh - 180rpx);
		padding: 0 30rpx 30rpx;
	}

	.stats-section {
		display: flex;
		justify-content: space-between;
		margin-bottom: 36rpx;
		background-color: #fff;
		border-radius: 16rpx;
		padding: 24rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);

		.stat-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			flex: 1;
			padding: 0 8rpx;

			.stat-icon {
				width: 56rpx;
				height: 56rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 32rpx;
				margin-bottom: 12rpx;
			}

			.stat-value {
				font-size: 32rpx;
				font-weight: 700;
				color: #2E7D32;
				margin-bottom: 6rpx;
			}

			.stat-label {
				font-size: 22rpx;
				color: #666;
				opacity: 0.8;
			}
		}
	}

	.features-section {
		margin-bottom: 36rpx;

		.feature-card {
			display: flex;
			align-items: center;
			padding: 32rpx 28rpx;
			background-color: #fff;
			border-radius: 16rpx;
			margin-bottom: 20rpx;
			box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
			transition: all 0.2s;

			&:active {
				transform: scale(0.98);
				box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
			}

			&:last-child {
				margin-bottom: 0;
			}

			.card-icon {
				width: 72rpx;
				height: 72rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 40rpx;
				margin-right: 28rpx;
				flex-shrink: 0;
			}

			.card-content {
				flex: 1;

				.card-title {
					display: block;
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
					margin-bottom: 8rpx;
					line-height: 1.3;
				}

				.card-desc {
					display: block;
					font-size: 24rpx;
					color: #666;
					opacity: 0.8;
					line-height: 1.4;
				}
			}
		}
	}

	.activity-section {
		.section-title-wrapper {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 28rpx;

			.section-title {
				font-size: 34rpx;
				font-weight: 600;
				color: #333;
			}

			.section-count {
				font-size: 24rpx;
				color: #999;
			}
		}

		.activity-item {
			display: flex;
			align-items: flex-start;
			padding: 28rpx;
			background-color: #fff;
			border-radius: 16rpx;
			margin-bottom: 20rpx;
			box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

			&:last-child {
				margin-bottom: 0;
			}

			.activity-icon-wrapper {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-right: 20rpx;
				flex-shrink: 0;

				.activity-icon {
					width: 12rpx;
					height: 12rpx;
					border-radius: 50%;
					position: relative;

					&::before {
						content: '';
						position: absolute;
						top: -4rpx;
						left: -4rpx;
						right: -4rpx;
						bottom: -4rpx;
						border-radius: 50%;
						background-color: currentColor;
						opacity: 0.2;
					}

					&.icon-green {
						background-color: #4CAF50;
						color: #4CAF50;
					}

					&.icon-blue {
						background-color: #2196F3;
						color: #2196F3;
					}

					&.icon-purple {
						background-color: #9C27B0;
						color: #9C27B0;
					}
				}
			}

			.activity-info {
				flex: 1;

				.activity-text {
					display: block;
					font-size: 28rpx;
					color: #333;
					margin-bottom: 8rpx;
					line-height: 1.4;
				}

				.activity-time {
					display: block;
					font-size: 22rpx;
					color: #999;
					opacity: 0.8;
				}
			}
		}
	}

	.footer-space {
		height: 40rpx;
	}
}
</style>
