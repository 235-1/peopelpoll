import request from "@/api/index"

export  const getLabelCount = async () => {
  const res = await request.get("/labelCount");
  return res.data;
}

