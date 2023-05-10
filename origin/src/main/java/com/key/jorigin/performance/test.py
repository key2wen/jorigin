import time

import numpy as np
import matplotlib.pyplot as plt
import json


## https://zhuanlan.zhihu.com/p/139052035

def format_plt():
    plt.xlabel('时间')
    # range 表示每隔x个点显示一次坐标，不然坐标之间间隔太近了
    plt.xticks(range(0, len(metric_time), 100), rotation=70)

    plt.legend()


if __name__ == '__main__':
    # x = np.linspace(0, 2, 100)
    #
    # plt.plot(x, x, label='linear')
    # plt.plot(x, x ** 2, label='quadratic')
    # plt.plot(x, x ** 3, label='cubic')
    #
    # plt.xlabel('x label')
    # plt.ylabel('y label')
    #
    # plt.title("Simple Plot")
    #

    #
    # plt.show()
    plt.rcParams['font.sans-serif'] = ['Arial Unicode MS']
    metric_time = []
    segment_apply_acount = []
    load_segment_from_remote = []
    load_segment_from_buffer = []

    # generate id count
    generate_id_count = []
    generate_id_fail_count = []
    generate_id_success_count = []

    # generate id cost time
    generate_id_cost_avg = []
    generate_id_cost_max = []

    id_total_count = 0

    with open("/Users/zhangwenhui/Downloads/global_id_metrics.json", "r") as f:
        json_str = f.read()
        metrics = json.loads(json_str)
        for metric in metrics:
            time_local = time.localtime(metric["ts"] / 1000)
            metric_time.append(time.strftime("%H:%M:%S", time_local))
            segment_apply_acount.append(metric["segmentApplyCount"])
            load_segment_from_remote.append(metric["loadFromRemoteCount"])
            load_segment_from_buffer.append(metric["loadFromBufferCount"])
            generate_id_count.append(metric["generateIdCount"])
            generate_id_fail_count.append(metric["generateIdFailCount"])
            generate_id_success_count.append(metric["generateIdSuccessCount"])
            id_total_count += metric["generateIdCount"]

            # generate id cost
            generate_id_cost_avg.append(metric["generateIdCostTotalInMs"] / metric["generateIdCount"])
            generate_id_cost_max.append(metric["generateIdCostMaxInMs"])
        print("一共生成了", id_total_count, "个id")
        fig = plt.figure(figsize=(15, 10))
        ax1 = fig.add_subplot(2, 2, 1)
        # ax1.plot(metric_time, segment_apply_acount, label="segment 申请次数")
        ax1.plot(metric_time, segment_apply_acount, "r-.d", label="segment 申请次数")
        # ax1.plot(metric_time, load_segment_from_buffer, label="buffer加载segment次数")
        ax1.plot(metric_time, load_segment_from_buffer, "c-d", label="buffer加载segment次数")
        ax1.plot(metric_time, load_segment_from_remote, label="远端加载segment")

        format_plt()

        # ax.spines["left"].set_color("darkblue")#设置左轴的颜色，我们图中未用
        # ax1.spines["bottom"].set_linewidth(3) # 底轴线条宽度设置

        ax1.spines["top"].set_visible(False)  # 上轴不显示
        ax1.spines["right"].set_visible(False)  # 右
        ax1.spines["left"].set_visible(False)  # 左

        # plt.title("global id segment申请统计")
        ax1.set_title("某某水果店一周水果销售量统计图", fontsize=18, backgroundcolor="#3c7f99",
                      fontweight="bold", color="white", verticalalignment="baseline")
        # ax1.set_xlabel("星期")  # 添加x轴坐标标签，后面看来没必要会删除它，这里只是为了演示一下。
        # ax1.set_ylabel("销售量", fontsize=16)  # 添加y轴标签，设置字体大小为16，这里也可以设字体样式与颜色

        # id 生成次数
        ax3 = fig.add_subplot(2, 2, 3)
        ax3.plot(metric_time, generate_id_count, label="id 生成次数")
        ax3.plot(metric_time, generate_id_fail_count, label="id 生成失败次数")
        ax3.plot(metric_time, generate_id_success_count, label="id 生成成功次数")
        format_plt()
        plt.title("global id 生成次数统计")

        # time cost
        ax2 = fig.add_subplot(2, 2, 2)
        ax2.plot(metric_time, generate_id_cost_avg, label="id 生成平均耗时")
        ax2.plot(metric_time, generate_id_cost_max, label="id 生成最大耗时")
        format_plt()
        plt.title("global id 生成耗时统计")
        # ax2.xlable('时间')

        # rotation 用于旋转，第一个参数控制x轴数据的个数
        # fig.autofmt_xdate()
        # plt.ylabel('y label')

        plt.show()
