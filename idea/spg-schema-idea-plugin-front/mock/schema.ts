// @ts-nocheck
import BaiKe from "./data/BaiKe.json"
import CsQa from "./data/CsQa.json"
import DomainKG from "./data/DomainKG.json"
import HotpotQA from "./data/HotpotQA.json"
import Medicine from "./data/Medicine.json"
import MuSiQue from "./data/MuSiQue.json"
import RiskMining from "./data/RiskMining.json"
import SupplyChain from "./data/SupplyChain.json"
import TwoWiki from "./data/TwoWiki.json"

export const responseBody = [
	BaiKe, CsQa, DomainKG, HotpotQA, Medicine, MuSiQue, RiskMining, SupplyChain, TwoWiki
]

export default [{
	url: '/openspg/api/schema/fetch',
	method: 'post',
	response: () => {
		return BaiKe
	}
}, {
	url: '/openspg/api/schema/focus',
	method: 'post',
	response: () => {
		return true
	}
}]
