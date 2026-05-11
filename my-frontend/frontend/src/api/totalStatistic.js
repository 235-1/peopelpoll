import request from "@/api/index";
// 请求微博整体数据统计
export const getWeiboTotalStatistic = () => {
  const res = request.get('/weiboTotalStatistic');
  console.log(res.data);
  return res.data;
}
